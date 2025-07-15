package uz.expense.controller;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@jakarta.ws.rs.Path("/upload")
public class UploadResource {

    @Inject
    @Location("upload-page.html")
    Template uploadPageTemplate;

    @Inject
    @Location("success.html")
    Template successTemplate;

    @Inject
    @Location("error.html")
    Template errorTemplate;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance uploadPage() {

        return uploadPageTemplate.instance();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_HTML)
    public Response handleFileUpload(@RestForm("file") FileUpload fileUpload,
                                     @RestForm("readMode") String readMode) {

        if (fileUpload == null || fileUpload.fileName() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorTemplate.instance()
                            .data("errorMessage", "No file uploaded"))
                    .build();
        }

        try {
            // Create uploads directory
            Path uploadsDir = Paths.get("uploads");
            Files.createDirectories(uploadsDir);

            // Get file info
            String fileName = fileUpload.fileName();
            String mimeType = fileUpload.contentType();
            long fileSize = fileUpload.size();

            // Save the uploaded file
            Path savedFile = uploadsDir.resolve("uploaded_" + fileName);
            Files.copy(fileUpload.uploadedFile(), savedFile, StandardCopyOption.REPLACE_EXISTING);

            // Analyze the file content
            FileAnalysis analysis = analyzeFileContent(savedFile, readMode, mimeType);

            // Create response
            return Response.ok(successTemplate.instance()
                            .data("fileName", fileName)
                            .data("mimeType", mimeType)
                            .data("fileSize", fileSize)
                            .data("analysis", analysis))
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(errorTemplate.instance()
                            .data("errorMessage", "File upload failed: " + e.getMessage()))
                    .build();
        }
    }

    private FileAnalysis analyzeFileContent(Path filePath, String readMode, String mimeType) throws IOException {

        FileAnalysis analysis = new FileAnalysis();

        // Determine reading strategy
        boolean isTextFile = isTextFile(mimeType);
        boolean shouldReadAsText = readMode.equals("text") || (readMode.equals("auto") && isTextFile);

        analysis.mimeType = mimeType;
        analysis.isTextFile = isTextFile;
        analysis.readingMode = shouldReadAsText ? "Text-based" : "Byte-based";
        analysis.shouldReadAsText = shouldReadAsText;

        if (shouldReadAsText) {
            analysis.textAnalysis = readAsText(filePath);
        } else {
            analysis.binaryAnalysis = readAsBytes(filePath);
        }

        return analysis;
    }

    private TextAnalysis readAsText(Path filePath) throws IOException {

        TextAnalysis result = new TextAnalysis();

        // Method 1: Using BufferedReader (character-based)
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            StringBuilder preview = new StringBuilder();
            String line;
            int lineCount = 0;
            while ((line = reader.readLine()) != null && lineCount < 10) {
                preview.append(line).append("\n");
                lineCount++;
            }
            result.preview = preview.toString();
            result.truncated = lineCount == 10;
        }

        // Method 2: Read entire file as String
        String content = Files.readString(filePath, StandardCharsets.UTF_8);
        result.characterCount = content.length();
        result.lineCount = content.split("\n").length;
        result.wordCount = content.split("\\s+").length;

        return result;
    }

    private BinaryAnalysis readAsBytes(Path filePath) throws IOException {

        BinaryAnalysis result = new BinaryAnalysis();

        byte[] fileBytes = Files.readAllBytes(filePath);
        result.fileSize = fileBytes.length;

        // First 50 bytes in hex
        int displayBytes = Math.min(50, fileBytes.length);
        StringBuilder hexPreview = new StringBuilder();
        for (int i = 0; i < displayBytes; i++) {
            hexPreview.append(String.format("%02X ", fileBytes[i]));
        }
        if (fileBytes.length > 50) {
            hexPreview.append("...");
        }
        result.hexPreview = hexPreview.toString();

        // Try to show some content if it's partially readable
        try {
            result.textPreview = new String(fileBytes, 0, Math.min(200, fileBytes.length), StandardCharsets.UTF_8);
            result.isReadableAsText = true;
        } catch (Exception e) {
            result.isReadableAsText = false;
        }

        return result;
    }

    private boolean isTextFile(String mimeType) {

        return mimeType != null && (
                mimeType.startsWith("text/") ||
                        mimeType.equals("application/json") ||
                        mimeType.equals("application/xml") ||
                        mimeType.equals("application/javascript") ||
                        mimeType.equals("application/csv")
        );
    }

    // Data classes for template binding
    public static class FileAnalysis {

        public String mimeType;

        public boolean isTextFile;

        public String readingMode;

        public boolean shouldReadAsText;

        public TextAnalysis textAnalysis;

        public BinaryAnalysis binaryAnalysis;

    }

    public static class TextAnalysis {

        public String preview;

        public boolean truncated;

        public int characterCount;

        public int lineCount;

        public int wordCount;

    }

    public static class BinaryAnalysis {

        public long fileSize;

        public String hexPreview;

        public String textPreview;

        public boolean isReadableAsText;

    }

}