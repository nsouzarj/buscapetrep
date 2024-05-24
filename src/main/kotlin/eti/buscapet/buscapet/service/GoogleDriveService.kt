package eti.buscapet.buscapet.service

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.InputStreamContent
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.IOException


@Service
@PropertySource("classpath:contafile.json")
class GoogleDriveService {

    @Value("\${google.drive.service.account.file}")
    private val serviceAccountFile: String? = null
    private var driveService: Drive? = null

    fun getDriveService(): Drive? {
        if (driveService == null) {
            try {
                driveService = initializeDriveService()
            } catch (e: IOException) {
                System.err.println("Error initializing Google Drive service: " + e.message)
            }
        }
        return driveService
    }

    private fun initializeDriveService(): Drive? {
        return try {
            val credentials = GoogleCredential.fromStream(FileInputStream(serviceAccountFile))
                .createScoped(listOf(DriveScopes.DRIVE_FILE)) // Use appropriate scope
            Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credentials
            )
                .setApplicationName(APPLICATION_NAME)
                .build()
        } catch (e: IOException) {
            System.err.println("Error initializing Drive service: " + e.message)
            null
        }
    }

    fun uploadFile(file: MultipartFile?): String? {
        try {
            if (file != null) {
                val fileMetadata = File()
                fileMetadata.setParents(listOf(FOLDER_GOOGLE))
                fileMetadata.setName(file.originalFilename)
                val uploadFile = getDriveService()
                    ?.files()
                    ?.create(
                        fileMetadata, InputStreamContent(
                            file.contentType,
                            ByteArrayInputStream(file.bytes)
                        )
                    )
                    ?.setFields("id")
                    ?.execute()
                println(uploadFile)
                if (uploadFile != null) {
                    return uploadFile.id
                }
            }
        } catch (e: IOException) {
            System.err.println("Error uploading file: " + e.message)
        } catch (e: GoogleJsonResponseException) {
            System.err.println("Error in Google Drive response: " + e.details.toString())
        }
        return null
    }

    fun downloadFile(fileId: String?, folderId: String?, retries: Int = 3): ByteArrayOutputStream? {
        for (attempt in 1..retries) {
            try {
                val file = getDriveService()!!.files()[fileId].execute()
                println("File: $file")
                println("Parents: " + file.parents)

                if (file.parents.contains(folderId)) {
                    val outputStream = ByteArrayOutputStream()
                    getDriveService()!!.files()[fileId].executeMediaAndDownloadTo(outputStream)
                    return outputStream
                } else {
                    System.err.println("File not in specified folder. Attempt: $attempt")
                    Thread.sleep((1000 * attempt).toLong())
                }
            } catch (e: IOException) {
                System.err.println("Error downloading file: " + e.message)
            } catch (e: GoogleJsonResponseException) {
                System.err.println("Error downloading file: " + e.details.toString())
            } catch (e: InterruptedException) {
                System.err.println("Thread interrupted: " + e.message)
            }
        }
        return null
    }


    fun listFilesInFolder(): MutableList<File>? {
        return try {
            val query = "parents in '" + FOLDER_GOOGLE + "'"
            val fields = "files(id, name, mimeType, size)"
            val files = driveService!!.files().list()
                .setQ(query)
                .setFields(fields)
                .setPageSize(100)
                .execute()
            files.files
        } catch (e: IOException) {
            System.err.println("Error listing files: " + e.message)
            null
        } catch (e: GoogleJsonResponseException) {
            System.err.println("Error listing files: " + e.details.toString())
            null
        }
    }

    fun downloadFileByName(fileName: String, folderId: String, retries: Int = 3): ByteArrayOutputStream? {
        for (attempt in 1..retries) {
            try {
                val fileList =
                    driveService!!.files().list().setQ("name = '$fileName' and parents in '$folderId'").execute()
                val file = fileList.files.stream().findFirst().orElse(null) ?: return null
                println("File: $fileList")
                println("Parents: " + file.parents)

                val outputStream = ByteArrayOutputStream()
                getDriveService()!!.files()[file.id].executeMediaAndDownloadTo(outputStream)

                if (outputStream.size() == 0) {
                    System.err.println("Error downloading file: File is empty.")
                    return null
                }
                return outputStream
            } catch (e: IOException) {
                System.err.println("Error downloading file: " + e.message)
            } catch (e: GoogleJsonResponseException) {
                System.err.println("Error downloading file: " + e.details.toString())
            } catch (e: NoSuchElementException) {
                System.err.println("File not found in folder. Attempt: $attempt")
                Thread.sleep((1000 * attempt).toLong())
            } catch (e: InterruptedException) {
                System.err.println("Thread interrupted: " + e.message)
            }
        }
        return null
    }

    companion object {
        private const val APPLICATION_NAME = "Buscapet"
        private val JSON_FACTORY: JsonFactory = JacksonFactory.getDefaultInstance()
        private const val FOLDER_GOOGLE = "1w667RDrWL-zIUdNg3Bi7mpYcccOps7B3" // Replace with your Google Drive folder ID
    }
}