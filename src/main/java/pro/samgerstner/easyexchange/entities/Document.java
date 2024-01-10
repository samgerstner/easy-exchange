package pro.samgerstner.easyexchange.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "documents")
public class Document
{
   @Id
   private String guid;

   @Column(name = "file_name", nullable = false)
   private String fileName;

   @Column(name = "file_type", nullable = false)
   private String fileType;

   @Column(name = "upload_at", nullable = false)
   private String uploadedAt;

   @Column(name = "download_nonce")
   private String downloadNonce;

   @ManyToOne
   private UploadSession uploadSession;

   public String getGuid()
   {
      return guid;
   }

   public void setGuid(String guid)
   {
      this.guid = guid;
   }

   public String getFileName()
   {
      return fileName;
   }

   public void setFileName(String fileName)
   {
      this.fileName = fileName;
   }

   public String getFileType()
   {
      return fileType;
   }

   public void setFileType(String fileType)
   {
      this.fileType = fileType;
   }

   public String getUploadedAt()
   {
      return uploadedAt;
   }

   public void setUploadedAt(String uploadedAt)
   {
      this.uploadedAt = uploadedAt;
   }

   public String getDownloadNonce()
   {
      return downloadNonce;
   }

   public void setDownloadNonce(String downloadNonce)
   {
      this.downloadNonce = downloadNonce;
   }

   public UploadSession getUploadSession()
   {
      return uploadSession;
   }

   public void setUploadSession(UploadSession uploadSession)
   {
      this.uploadSession = uploadSession;
   }
}
