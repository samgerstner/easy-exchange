package pro.samgerstner.easyexchange.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "application_roles")
public class ApplicationRole
{
   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private Integer id;

   @Column(name = "role_name", nullable = false)
   private String roleName;

   @OneToMany
   private List<AdminUser> users;

   public ApplicationRole() {}

   public ApplicationRole(int id, String name)
   {
      this.id = id;
      this.roleName = name;
   }

   public Integer getId()
   {

      return id;
   }

   public void setId(Integer id)
   {

      this.id = id;
   }

   public String getRoleName()
   {

      return roleName;
   }

   public void setRoleName(String roleName)
   {

      this.roleName = roleName;
   }

   public List<AdminUser> getUsers()
   {

      return users;
   }

   public void setUsers(List<AdminUser> users)
   {

      this.users = users;
   }
}