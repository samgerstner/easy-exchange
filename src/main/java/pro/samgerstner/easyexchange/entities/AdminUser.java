package pro.samgerstner.easyexchange.entities;

import jakarta.persistence.*;

import java.util.Random;

@Entity
@Table(name = "admin_users")
public class AdminUser
{
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private Integer id;

   @Column(name = "username", nullable = false, unique = true)
   private String username;

   @Column(name = "api_enabled", nullable = false)
   private boolean apiEnabled;

   @Column(name = "api_key", unique = true)
   private String apiKey;

   @ManyToOne
   private ApplicationRole userRole;

   public Integer getId()
   {

      return id;
   }

   public void setId(Integer id)
   {

      this.id = id;
   }

   public String getUsername()
   {

      return username;
   }

   public void setUsername(String username)
   {

      this.username = username;
   }

   public boolean isApiEnabled()
   {

      return apiEnabled;
   }

   public void setApiEnabled(boolean apiEnabled)
   {

      this.apiEnabled = apiEnabled;
   }

   public String getApiKey()
   {

      return apiKey;
   }

   public void setApiKey(String apiKey)
   {

      this.apiKey = apiKey;
   }

   public ApplicationRole getUserRole()
   {

      return userRole;
   }

   public void setUserRole(ApplicationRole userRole)
   {

      this.userRole = userRole;
   }

   public void populateApiKey()
   {
      String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
      StringBuilder builder = new StringBuilder();
      Random random = new Random();

      while(builder.length() < 64)
      {
         int index = (int) (random.nextFloat() * chars.length());
         builder.append(chars.charAt(index));
      }

      this.apiKey = builder.toString();
   }
}