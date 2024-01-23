package pro.samgerstner.easyexchange.entities.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SessionCreateRequest
{
   @JsonProperty("max_documents")
   private int maxDocuments;

   @JsonProperty("upload_locked")
   private boolean uploadLocked;

   @JsonProperty("download_locked")
   private boolean downloadLocked;

   @JsonProperty("client_id")
   private int clientId;

   public SessionCreateRequest(int maxDocuments, boolean uploadLocked, boolean downloadLocked, int clientId)
   {
      this.maxDocuments = maxDocuments;
      this.uploadLocked = uploadLocked;
      this.downloadLocked = downloadLocked;
      this.clientId = clientId;
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

   public int getClientId()
   {
      return clientId;
   }

   public void setClientId(int clientId)
   {
      this.clientId = clientId;
   }
}