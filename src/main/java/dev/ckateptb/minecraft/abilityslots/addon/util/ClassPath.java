package dev.ckateptb.minecraft.abilityslots.addon.util;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

public final class ClassPath {
    private static final Logger logger = Logger.getLogger(ClassPath.class.getName());

    private static final Splitter CLASS_PATH_ATTRIBUTE_SEPARATOR = Splitter.on(" ").omitEmptyStrings();

    private static final String CLASS_FILE_NAME_EXTENSION = ".class";

    private final ImmutableSet<ResourceInfo> resources;

    private ClassPath(ImmutableSet<ResourceInfo> resources) {
        this.resources = resources;
    }

    public static ClassPath from(ClassLoader classloader, JarFile jarFile) {
        DefaultScanner scanner = new DefaultScanner();
        scanner.scanJarFile(classloader, jarFile);
        return new ClassPath(scanner.getResources());
    }

    @VisibleForTesting
    static String getClassName(String filename) {
        int classNameEnd = filename.length() - CLASS_FILE_NAME_EXTENSION.length();
        return filename.substring(0, classNameEnd).replace('/', '.');
    }

    public Set<ClassInfo> getAllClasses() {
        return resources.stream().filter(ClassInfo.class::isInstance).map(ClassInfo.class::cast).collect(Collectors.toSet());
    }

    @Beta
    public static class ResourceInfo {
        final ClassLoader loader;
        private final String resourceName;

        ResourceInfo(String resourceName, ClassLoader loader) {
            this.resourceName = checkNotNull(resourceName);
            this.loader = checkNotNull(loader);
        }

        static ResourceInfo of(String resourceName, ClassLoader loader) {
            if (resourceName.endsWith(CLASS_FILE_NAME_EXTENSION)) {
                return new ClassInfo(resourceName, loader);
            } else {
                return new ResourceInfo(resourceName, loader);
            }
        }

        @Override
        public int hashCode() {
            return resourceName.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ResourceInfo that) {
                return resourceName.equals(that.resourceName) && loader == that.loader;
            }
            return false;
        }

        @Override
        public String toString() {
            return resourceName;
        }
    }

    public static final class ClassInfo extends ResourceInfo {
        private final String className;

        ClassInfo(String resourceName, ClassLoader loader) {
            super(resourceName, loader);
            this.className = getClassName(resourceName);
        }

        public String getName() {
            return className;
        }

        public Class<?> load() {
            try {
                return loader.loadClass(className);
            } catch (ClassNotFoundException e) {
                // Shouldn't happen, since the class name is read from the class path.
                throw new IllegalStateException(e);
            }
        }

        @Override
        public String toString() {
            return className;
        }
    }

    abstract static class Scanner {

        private final Set<File> scannedUris = Sets.newHashSet();

        @VisibleForTesting
        static ImmutableSet<File> getClassPathFromManifest(File jarFile, @Nullable Manifest manifest) {
            if (manifest == null) {
                return ImmutableSet.of();
            }
            ImmutableSet.Builder<File> builder = ImmutableSet.builder();
            String classpathAttribute =
                    manifest.getMainAttributes().getValue(Attributes.Name.CLASS_PATH.toString());
            if (classpathAttribute != null) {
                for (String path : CLASS_PATH_ATTRIBUTE_SEPARATOR.split(classpathAttribute)) {
                    URL url;
                    try {
                        url = getClassPathEntry(jarFile, path);
                    } catch (MalformedURLException e) {
                        logger.warning("Invalid Class-Path entry: " + path);
                        continue;
                    }
                    if (url.getProtocol().equals("file")) {
                        builder.add(new File(url.getFile()));
                    }
                }
            }
            return builder.build();
        }

        @VisibleForTesting
        static URL getClassPathEntry(File jarFile, String path) throws MalformedURLException {
            return new URL(jarFile.toURI().toURL(), path);
        }

        protected abstract void scanDirectory(ClassLoader loader, File directory) throws IOException;

        protected abstract void scanJarFile(ClassLoader loader, JarFile file) throws IOException;

        @VisibleForTesting
        final void scan(File file, ClassLoader classloader) throws IOException {
            if (scannedUris.add(file.getCanonicalFile())) {
                scanFrom(file, classloader);
            }
        }

        private void scanFrom(File file, ClassLoader classloader) throws IOException {
            try {
                if (!file.exists()) {
                    return;
                }
            } catch (SecurityException e) {
                logger.warning("Cannot access " + file + ": " + e);
                return;
            }
            if (file.isDirectory()) {
                scanDirectory(classloader, file);
            } else {
                scanJar(file, classloader);
            }
        }

        private void scanJar(File file, ClassLoader classloader) throws IOException {
            JarFile jarFile;
            try {
                jarFile = new JarFile(file);
            } catch (IOException e) {
                return;
            }
            try {
                for (File path : getClassPathFromManifest(file, jarFile.getManifest())) {
                    scan(path, classloader);
                }
                scanJarFile(classloader, jarFile);
            } finally {
                try {
                    jarFile.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    @VisibleForTesting
    static final class DefaultScanner extends Scanner {
        private final SetMultimap<ClassLoader, String> resources = MultimapBuilder.hashKeys().linkedHashSetValues().build();

        ImmutableSet<ResourceInfo> getResources() {
            ImmutableSet.Builder<ResourceInfo> builder = ImmutableSet.builder();
            for (Map.Entry<ClassLoader, String> entry : resources.entries()) {
                builder.add(ResourceInfo.of(entry.getValue(), entry.getKey()));
            }
            return builder.build();
        }

        @Override
        protected void scanJarFile(ClassLoader classloader, JarFile file) {
            Enumeration<JarEntry> entries = file.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory() || entry.getName().equals(JarFile.MANIFEST_NAME)) {
                    continue;
                }
                resources.get(classloader).add(entry.getName());
            }
        }

        @Override
        protected void scanDirectory(ClassLoader classloader, File directory) {
            scanDirectory(directory, classloader, "");
        }

        private void scanDirectory(File directory, ClassLoader classloader, String packagePrefix) {
            File[] files = directory.listFiles();
            if (files == null) {
                logger.warning("Cannot read directory " + directory);
                return;
            }
            for (File f : files) {
                String name = f.getName();
                if (f.isDirectory()) {
                    scanDirectory(f, classloader, packagePrefix + name + "/");
                } else {
                    String resourceName = packagePrefix + name;
                    if (!resourceName.equals(JarFile.MANIFEST_NAME)) {
                        resources.get(classloader).add(resourceName);
                    }
                }
            }
        }
    }
}