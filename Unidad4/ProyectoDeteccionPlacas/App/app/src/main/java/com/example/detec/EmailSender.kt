package com.example.detec

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Properties
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object EmailSender {

    private const val MI_CORREO = "lizhh.raak@gmail.com"
    private const val MI_PASSWORD = "cogo lezm hvbv qfla"

    // Función suspendida para no trabar la app
    suspend fun enviar(destinatario: String, placa: String, descripcion: String) {
        withContext(Dispatchers.IO) {
            try {
                val props = Properties().apply {
                    put("mail.smtp.host", "smtp.gmail.com")
                    put("mail.smtp.socketFactory.port", "465")
                    put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
                    put("mail.smtp.auth", "true")
                    put("mail.smtp.port", "465")
                }

                val session = Session.getInstance(props, object : javax.mail.Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(MI_CORREO, MI_PASSWORD)
                    }
                })

                // Crear el mensaje
                val mensaje = MimeMessage(session).apply {
                    setFrom(InternetAddress(MI_CORREO))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario))
                    subject = "Confirmación de Reporte: $placa"
                    setText("""
                        Hola,
                        
                        Tu reporte ha sido registrado exitosamente desde la App deTec.
                        
                        -------------------------
                        🚗 PLACA: $placa
                        📝 DESCRIPCIÓN: $descripcion
                        -------------------------
                        
                        Gracias por tu colaboración.
                    """.trimIndent())
                }

                // Enviar
                Transport.send(mensaje)
                println("✅ Correo enviado exitosamente desde Android a $destinatario")

            } catch (e: Exception) {
                e.printStackTrace()
                println("❌ Error enviando correo: ${e.message}")
            }
        }
    }
}