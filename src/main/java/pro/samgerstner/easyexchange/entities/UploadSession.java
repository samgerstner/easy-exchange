package pro.samgerstner.easyexchange.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "upload_sessions")
public class UploadSession
{
   @Id
   private String guid;

   @Column(name = "max_documents", nullable = false)
   private int maxDocuments;

   @Column(name = "upload_locked", nullable = false)
   private boolean uploadLocked;

   @Column(name = "download_locked", nullable = false)
   private boolean downloadLocked;

   @ManyToOne
   private Client client;

   @OneToMany
   private List<Document> documents;

   public String getGuid()
   {
      return guid;
   }

   public void setGuid(String guid)
   {
      this.guid = guid;
   }

   public int getMaxDocuments()
   {
      return maxDocuments;
   }

   public void setMaxDocuments(int maxDocuments)
   {
      this.maxDocuments = maxDocuments;
   }

   public boolean isUploadLocked()
   {
      return uploadLocked;
   }

   public void setUploadLocked(boolean uploadLocked)
   {
      this.uploadLocked = uploadLocked;
   }

   public boolean isDownloadLocked()
   {
      return downloadLocked;
   }

   public void setDownloadLocked(boolean downloadLocked)
   {
      this.downloadLocked = downloadLocked;
   }

   public Client getClient()
   {
      return client;
   }

   public void setClient(Client client)
   {
      this.client = client;
   }

   public List<Document> getDocuments()
   {
      return documents;
   }

   public void setDocuments(List<Document> documents)
   {
      this.documents = documents;
   }
}
