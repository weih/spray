package spray.io.openssl
package api

import org.bridj.{Pointer, TypedPointer}
import LibSSL._

class SSLCtx private[openssl](pointer: Long) extends TypedPointer(pointer) {
  def newSSL(): SSL = {
    val ssl = SSL_new(getPeer).returnChecked
    require(ssl != 0L)
    new SSL(ssl)
  }

  def setDefaultVerifyPaths(): Unit =
    SSL_CTX_set_default_verify_paths(getPeer).returnChecked

  def setVerify(mode: Int) {
    SSL_CTX_set_verify(getPeer, mode, 0)
  }

  def setCipherList(ciphers: DirectBuffer): Unit =
    SSL_CTX_set_cipher_list(getPeer, ciphers.pointer.getPeer).returnChecked

  def usePrivateKeyFile(fileName: String, `type`: Int): Unit = {
    val buffer = Pointer.allocateBytes(fileName.length + 1)
    buffer.setCString(fileName)
    SSL_CTX_use_PrivateKey_file(getPeer, buffer, `type`).returnChecked
  }
  def useCertificateChainFile(fileName: String): Unit = {
    val buffer = Pointer.allocateBytes(fileName.length + 1)
    buffer.setCString(fileName)
    SSL_CTX_use_certificate_chain_file(getPeer, buffer).returnChecked
  }

  import SSL._
  def setOptions(options: Long): Long =
    SSL_CTX_ctrl(getPeer, SSL_CTRL_OPTIONS, options, 0)

  def setMode(mode: Long): Long =
    SSL_CTX_ctrl(getPeer, SSL_CTRL_MODE, mode, 0)

  def getCertificateStore: X509_STORE =
    SSL_CTX_get_cert_store(getPeer)
}
object SSLCtx {
  // make sure openssl is initialized
  OpenSSL()

  def create(method: Long): SSLCtx = new SSLCtx(SSL_CTX_new(method).returnChecked)
}
