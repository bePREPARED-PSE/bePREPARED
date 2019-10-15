package edu.kit.pse.beprepared.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * A service class that handles the files used in bePREPARED.
 */
public class FileManagementService {

    /**
     * The only instance of this class.
     */
    private static FileManagementService instance;
    /**
     * The {@link File}s managed by this service.
     */
    private final HashMap<String, File> files;
    /**
     * The (temporary) base directory.
     */
    private Path baseDirectory;


    /**
     * Constructor.
     */
    private FileManagementService() throws IOException {
        this.baseDirectory = Files.createTempDirectory("bePREPARED-working-dir");
        this.baseDirectory.toFile().deleteOnExit();
        this.files = new HashMap<>();
    }

    /**
     * Getter for {@link FileManagementService#instance}.
     *
     * @return the value of {@link FileManagementService#instance}
     */
    public static FileManagementService getInstance() throws IOException {
        if (FileManagementService.instance == null) {
            FileManagementService.instance = new FileManagementService();
        }
        return FileManagementService.instance;
    }

    /**
     * Store a {@link MultipartFile}.
     *
     * @param file           the file
     * @param name           the name the file should be mapped to
     * @param deleteIfExists if a file with the given name exists: delete it?
     * @return the name to which the file is mapped
     * @throws IOException if an exception occurs when handling the files
     */
    public String storeFile(MultipartFile file, String name, boolean deleteIfExists) throws IOException {

        if (name == null) {
            throw new NullPointerException("name must not be null!");
        }

        File tmpFile = this.createFile(name, true);
        file.transferTo(tmpFile);

        return this.storeFile(tmpFile, name, deleteIfExists);
    }

    /**
     * Store a {@link File}.
     *
     * @param file           the file
     * @param name           the name the file should be mapped to
     * @param deleteIfExists if a file with the supplied name exists: delete it?
     * @return the name to which the file is mapped
     * @throws IOException if an exception occurs when handling the files
     */
    public String storeFile(File file, String name, boolean deleteIfExists) throws IOException {

        if (this.files.containsKey(name)) {
            if (!deleteIfExists) {
                throw new FileAlreadyExistsException("file with name \"" + name + "\" already exists!");
            } else {
                Files.delete(this.files.get(name).toPath());
                this.files.remove(name);
            }
        }
//        Files.move(file.toPath(), this.baseDirectory);
        file.deleteOnExit();

        this.files.put(name, file);
        return name;
    }

    /**
     * Getter for the {@link File} mapped to the supplied name.
     *
     * @param name the name of the file
     * @return the file or {@code null}, if the name does not exist
     */
    public File getFile(String name) throws FileNotFoundException {

        if (this.files.containsKey(name)) {
            return this.files.get(name);
        }

        throw new FileNotFoundException("can not find name: " + name);
    }

    /**
     * Collect all the files with the supplied names.
     *
     * @param names a {@link Collection<String>} containing the names of the files that should be collected
     * @return a {@link Collection<File>} containing the files
     * @throws FileNotFoundException if one or more files could not be found in {@link this#files}
     */
    public Collection<File> collectFiles(Collection<String> names) throws FileNotFoundException {

        LinkedList<File> fileCollection = new LinkedList<>();
        for (String name : names) {
            if (name == null) {
                continue;
            }
            File f = this.files.get(name);
            if (f == null) {
                throw new FileNotFoundException("can not find file with name: " + name);
            } else {
                fileCollection.add(f);
            }
        }

        return fileCollection;
    }

    /**
     * Creates a zip file with the supplied name and containing the supplied files.
     *
     * @param name           the name the zip file should have
     * @param filesToZip     the files that should be compressed
     * @param deleteIfExists if a file with the given name exists: delete it?
     * @return the zip file
     * @throws IOException if anything goes wrong while handling the files
     */
    public File createZipFile(String name, Collection<File> filesToZip, boolean deleteIfExists) throws IOException {

        File zipFile = this.createFile(name, deleteIfExists);
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));

        for (File file : filesToZip) {

            FileInputStream fis = new FileInputStream(file);
            ZipEntry entry = new ZipEntry(file.getName());
            zos.putNextEntry(entry);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) >= 0) {
                zos.write(buffer, 0, len);
            }
            fis.close();
        }
        zos.close();

        return zipFile;
    }

    /**
     * Unzip the supplied {@link File}.
     *
     * @param file the file that should be unzipped
     * @return a collection of the files inside the zip archive
     * @throws IOException if something goes wrong handling the files
     */
    public LinkedList<File> unzipFile(File file) throws IOException {

        LinkedList<File> files = new LinkedList<>();

        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            File newFile = this.createFile(entry.getName(), true);
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            files.add(newFile);
        }

        return files;
    }

    /**
     * Creates a (temp) file located in {@link this#baseDirectory}.
     *
     * @param name           the filename
     * @param deleteIfExists if a file with the given name exists: delete it?
     * @return the file
     * @throws IOException if something goes wrong creating the file
     */
    public File createFile(String name, boolean deleteIfExists) throws IOException {

        Path path = Paths.get(this.baseDirectory.toAbsolutePath().toString(), name);

        if (Files.exists(path)) {
            if (deleteIfExists) {
                Files.delete(path);
            } else {
                throw new FileAlreadyExistsException("a file with the name \"" + name + "\" already exists");
            }
        }

        File file = Files.createFile(path).toFile();
        file.deleteOnExit();

        return file;
    }

}
