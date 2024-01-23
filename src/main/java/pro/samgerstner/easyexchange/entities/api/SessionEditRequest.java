package pro.samgerstner.easyexchange.entities.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SessionEditRequest
{
   private String guid;

   @JsonProperty("max_documents")
   private int maxDocuments;

   @JsonProperty("upload_locked")
   private boolean uploadLocked;

   @JsonProperty("download_locked")
   private boolean downloadLocked;

   @JsonProperty("client_id")
   private int clientId;

   public SessionEditRequest(String guid, int maxDocuments, boolean uploadLocked, boolean downloadLocked, int clientId)
   {
      this.guid = guid;
      this.maxDocuments = maxDocuments;
      this.uploadLocked = uploadLocked;
      this.downloadLocked = downloadLocked;
      this.clientId = clientId;
   }

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

   public int getClientId()
   {
      return clientId;
   }

   public void setClientId(int clientId)
   {
      this.clientId = clientId;
   }
}