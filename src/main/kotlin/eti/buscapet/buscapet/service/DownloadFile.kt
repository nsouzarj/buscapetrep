package eti.buscapet.buscapet.service

import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


/* Class to demonstrate use-case of drive's download file. */
object DownloadFile {
    /**
     * Download a Document file in PDF format.
     *
     * @param realFileId file ID of any workspace document format file.
     * @return byte array stream if successful, `null` otherwise.
     * @throws IOException if service account credentials file not found.
     */
    @Throws(IOException::class)
    fun downloadFile(realFileId: String?): ByteArrayOutputStream {
        /* Load pre-authorized user credentials from the environment.
           TODO(developer) - See https://developers.google.com/identity for
          guides on implementing OAuth2 for your application.*/
        val credentials: GoogleCredentials = GoogleCredentials.getApplicationDefault()
            .createScoped(Arrays.asList(DriveScopes.DRIVE_FILE))
        val requestInitializer: HttpRequestInitializer = HttpCredentialsAdapter(
            credentials
        )

        // Build a new authorized API client service.
        val service = Drive.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            requestInitializer
        )
            .setApplicationName("Drive samples")
            .build()
        return try {
            val outputStream: OutputStream = ByteArrayOutputStream()
            service.files()[realFileId]
                .executeMediaAndDownloadTo(outputStream)
            outputStream as ByteArrayOutputStream
        } catch (e: GoogleJsonResponseException) {
            // TODO(developer) - handle error appropriately
            System.err.println("Unable to move file: " + e.details)
            throw e
        }
    }
}