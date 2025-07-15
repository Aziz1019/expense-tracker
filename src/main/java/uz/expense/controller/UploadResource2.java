//package uz.expense.controller;
//
//import jakarta.ws.rs.*;
//import jakarta.ws.rs.core.MediaType;
//import jakarta.ws.rs.core.Response;
//import org.jboss.resteasy.reactive.RestForm;
//import org.jboss.resteasy.reactive.multipart.FileUpload;
//
//import java.io.*;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//
//@jakarta.ws.rs.Path("/upload")
//public class UploadResource {
//
//    @GET
//    @Produces(MediaType.TEXT_HTML)
//    public String uploadPage() {
//        return """
//            <!DOCTYPE html>
//            <html>
//            <head>
//                <title>File Upload - Byte vs Text Reading</title>
//                <style>
//                    body { font-family: Arial, sans-serif; margin: 40px; }
//                    .container { max-width: 600px; }
//                    .form-group { margin: 20px 0; }
//                    button { padding: 10px 20px; background: #007bff; color: white; border: none; cursor: pointer; }
//                    .result { margin-top: 20px; padding: 15px; background: #f8f9fa; border: 1px solid #dee2e6; }
//                </style>
//            </head>
//            <body>
//                <div class="container">
//                    <h2>File Upload - Learning Byte vs Text Reading</h2>
//                    <form action="/upload" method="post" enctype="multipart/form-data">
//                        <div class="form-group">
//                            <label for="file">Choose a file:</label><br>
//                            <input type="file" id="file" name="file" required>
//                        </div>
//                        <div class="form-group">
//                            <label for="readMode">Reading Mode:</label><br>
//                            <input type="radio" id="auto" name="readMode" value="auto" checked>
//                            <label for="auto">Auto-detect (recommended)</label><br>
//                            <input type="radio" id="byte" name="readMode" value="byte">
//                            <label for="byte">Force Byte Reading</label><br>
//                            <input type="radio" id="text" name="readMode" value="text">
//                            <label for="text">Force Text Reading</label>
//                        </div>
//                        <button type="submit">Upload & Analyze</button>
//                    </form>
//                </div>
//            </body>
//            </html>
//            """;
//    }
//
//    @POST
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    @Produces(MediaType.TEXT_HTML)
//    public Response handleFileUpload(@RestForm("file") FileUpload fileUpload,
//                                     @RestForm("readMode") String readMode) {
//
//        if (fileUpload == null || fileUpload.fileName() == null) {
//            return Response.status(Response.Status.BAD_REQUEST)
//                    .entity(createErrorResponse("No file uploaded"))
//                    .build();
//        }
//
//        try {
//            // Create uploads directory
//            Path uploadsDir = Paths.get("uploads");
//            Files.createDirectories(uploadsDir);
//
//            // Get file info
//            String fileName = fileUpload.fileName();
//            String mimeType = fileUpload.contentType();
//            long fileSize = fileUpload.size();
//
//            // Save the uploaded file
//            Path savedFile = uploadsDir.resolve("uploaded_" + fileName);
//            Files.copy(fileUpload.filePath(), savedFile, StandardCopyOption.REPLACE_EXISTING);
//
//            // Analyze the file content
//            String analysisResult = analyzeFileContent(savedFile, readMode, mimeType);
//
//            // Create response
//            String response = createSuccessResponse(fileName, mimeType, fileSize, analysisResult);
//
//            return Response.ok(response).build();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//                    .entity(createErrorResponse("File upload failed: " + e.getMessage()))
//                    .build();
//        }
//    }
//
//    private String analyzeFileContent(Path filePath, String readMode, String mimeType) throws IOException {
//        StringBuilder analysis = new StringBuilder();
//
//        // Determine reading strategy
//        boolean isTextFile = isTextFile(mimeType);
//        boolean shouldReadAsText = readMode.equals("text") || (readMode.equals("auto") && isTextFile);
//
//        analysis.append("<h3>File Analysis</h3>");
//        analysis.append("<p><strong>MIME Type:</strong> ").append(mimeType).append("</p>");
//        analysis.append("<p><strong>Detected as:</strong> ").append(isTextFile ? "Text file" : "Binary file").append("</p>");
//        analysis.append("<p><strong>Reading Mode:</strong> ").append(shouldReadAsText ? "Text-based" : "Byte-based").append("</p>");
//
//        if (shouldReadAsText) {
//            analysis.append(readAsText(filePath));
//        } else {
//            analysis.append(readAsBytes(filePath));
//        }
//
//        return analysis.toString();
//    }
//
//    private String readAsText(Path filePath) throws IOException {
//        StringBuilder result = new StringBuilder();
//        result.append("<h4>📄 Text-based Reading (Character Stream)</h4>");
//
//        // Method 1: Using BufferedReader (character-based)
//        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
//            result.append("<p><strong>Using BufferedReader:</strong></p>");
//            result.append("<pre style='background: #f0f0f0; padding: 10px; max-height: 200px; overflow-y: auto;'>");
//
//            String line;
//            int lineCount = 0;
//            while ((line = reader.readLine()) != null && lineCount < 10) {
//                result.append(escapeHtml(line)).append("\n");
//                lineCount++;
//            }
//            if (lineCount == 10) {
//                result.append("... (truncated, showing first 10 lines)");
//            }
//            result.append("</pre>");
//        }
//
//        // Method 2: Read entire file as String
//        String content = Files.readString(filePath, StandardCharsets.UTF_8);
//        result.append("<p><strong>File Statistics:</strong></p>");
//        result.append("<ul>");
//        result.append("<li>Character count: ").append(content.length()).append("</li>");
//        result.append("<li>Line count: ").append(content.split("\n").length).append("</li>");
//        result.append("<li>Word count: ").append(content.split("\\s+").length).append("</li>");
//        result.append("</ul>");
//
//        return result.toString();
//    }
//
//    private String readAsBytes(Path filePath) throws IOException {
//        StringBuilder result = new StringBuilder();
//        result.append("<h4>🔢 Byte-based Reading (Binary Stream)</h4>");
//
//        byte[] fileBytes = Files.readAllBytes(filePath);
//
//        result.append("<p><strong>File Statistics:</strong></p>");
//        result.append("<ul>");
//        result.append("<li>File size: ").append(fileBytes.length).append(" bytes</li>");
//        result.append("<li>First 50 bytes (hex): ");
//
//        int displayBytes = Math.min(50, fileBytes.length);
//        for (int i = 0; i < displayBytes; i++) {
//            result.append(String.format("%02X ", fileBytes[i]));
//        }
//        if (fileBytes.length > 50) {
//            result.append("...");
//        }
//        result.append("</li>");
//        result.append("</ul>");
//
//        // Try to show some content if it's partially readable
//        try {
//            String preview = new String(fileBytes, 0, Math.min(200, fileBytes.length), StandardCharsets.UTF_8);
//            result.append("<p><strong>Preview (if readable as UTF-8):</strong></p>");
//            result.append("<pre style='background: #f0f0f0; padding: 10px; max-height: 100px; overflow-y: auto;'>");
//            result.append(escapeHtml(preview));
//            result.append("</pre>");
//        } catch (Exception e) {
//            result.append("<p><em>Content is not readable as UTF-8 text</em></p>");
//        }
//
//        return result.toString();
//    }
//
//    private boolean isTextFile(String mimeType) {
//        return mimeType != null && (
//                mimeType.startsWith("text/") ||
//                        mimeType.equals("application/json") ||
//                        mimeType.equals("application/xml") ||
//                        mimeType.equals("application/javascript") ||
//                        mimeType.equals("application/csv")
//        );
//    }
//
//    private String escapeHtml(String text) {
//        return text.replace("&", "&amp;")
//                .replace("<", "&lt;")
//                .replace(">", "&gt;")
//                .replace("\"", "&quot;")
//                .replace("'", "&#39;");
//    }
//
//    private String createSuccessResponse(String fileName, String mimeType, long fileSize, String analysis) {
//        return """
//            <!DOCTYPE html>
//            <html>
//            <head>
//                <title>Upload Success</title>
//                <style>
//                    body { font-family: Arial, sans-serif; margin: 40px; }
//                    .container { max-width: 800px; }
//                    .success { background: #d4edda; border: 1px solid #c3e6cb; color: #155724; padding: 15px; margin: 20px 0; }
//                    .info { background: #f8f9fa; border: 1px solid #dee2e6; padding: 15px; margin: 20px 0; }
//                    a { color: #007bff; text-decoration: none; }
//                </style>
//            </head>
//            <body>
//                <div class="container">
//                    <div class="success">
//                        <h2>✅ File Upload Successful!</h2>
//                    </div>
//
//                    <div class="info">
//                        <h3>File Information</h3>
//                    </div>
//
//                    <div class="info">
//                        """ + analysis + """
//                    </div>
//
//                    <a href="/upload">← Upload Another File</a>
//                </div>
//            </body>
//            </html>
//            """;
//    }
//
//    private String createErrorResponse(String error) {
//        return """
//            <!DOCTYPE html>
//            <html>
//            <head>
//                <title>Upload Error</title>
//                <style>
//                    body { font-family: Arial, sans-serif; margin: 40px; }
//                    .error { background: #f8d7da; border: 1px solid #f5c6cb; color: #721c24; padding: 15px; margin: 20px 0; }
//                    a { color: #007bff; text-decoration: none; }
//                </style>
//            </head>
//            <body>
//                <div class="error">
//                    <h2>❌ Error</h2>
//                </div>
//                <a href="/upload">← Try Again</a>
//            </body>
//            </html>
//            """;
//    }
//}