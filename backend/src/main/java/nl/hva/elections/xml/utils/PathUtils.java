package nl.hva.elections.xml.utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * A helper-class used for traversing a directory structure which contains the Dutch election data.
 */
public class PathUtils {

    /**
     * Starting from @{code sourceLocation} searches the folder and any folder contained in it for files with the
     * specified {@code prefix}.
     * @param sourceLocation the starting point for the search.
     * @param prefix files beginning with this prefix are included in the resulting list.
     * @return A {@link List} containing all the files with the specified {@code prefix} find in this folder or any
     * folder contained in starting folder.
     * @throws IOException in case something went wrong while traversing the folders.
     */
    public static List<Path> findFilesToScan(String sourceLocation, String prefix) throws IOException {
        List<Path> filesToScan = new ArrayList<>();
        Files.walkFileTree(Path.of(sourceLocation), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String fileName = file.getFileName().toString();
                if (fileName.startsWith(prefix) && fileName.endsWith(".xml"))
                    filesToScan.add(file);
                return FileVisitResult.CONTINUE;
            }
        });
        return filesToScan;
    }

    /**
     * Transforms a {@code resourceName} into an absolute path of that resource. If it does not exist at the expected
     * location it tries to fall back to a folder called {@code data-files} or {@code Downloads}.
     * @param resourceName the resource to locate.
     * @return a fully qualified absolute path to the resource.
     */
    public static String getResourcePath(String resourceName) {
        // If it is an absolute directory name, we're done
        if (Path.of(resourceName).toFile().isDirectory()) {
            return resourceName;
        }
        try {
            URL url = PathUtils.class.getResource(resourceName);
            if (url != null) {
                return new File(url.toURI()).getPath();
            }

            // trim leading slashes to resolve relatively
            while (resourceName.startsWith("/")) resourceName = resourceName.substring(1);

            url = PathUtils.class.getResource("/");
            URI projectRootURI = url.toURI();
            String resourceFilePath = null;
            while (resourceFilePath == null && projectRootURI.getPath().length() > 15) {
                projectRootURI = projectRootURI.resolve("..");
                // try to find the resource in a data files folder up the project/file system tree
                resourceFilePath = new File(projectRootURI.resolve("data-files/").resolve(resourceName)).getPath();
                if (!Files.exists(Path.of(resourceFilePath))) {
                    resourceFilePath = new File(projectRootURI.resolve("Downloads/").resolve(resourceName)).getPath();
                }
                if (!Files.exists(Path.of(resourceFilePath))) {
                    resourceFilePath = null;
                }
            }

            return resourceFilePath;

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
