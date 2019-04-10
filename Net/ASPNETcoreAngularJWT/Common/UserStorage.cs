using System;
using System.Collections.Generic;

 namespace ASPNETCoreAngularJWT
{
 public static class UserStorage
    {
        public static List<User> Users { get; set; } = new List<User> {
            new User {ID=Guid.NewGuid(),Username="user1",Password = "password", Fname="user1", Lname="Lname1" },
            new User {ID=Guid.NewGuid(),Username="user2",Password = "password", Fname="user2", Lname="Lname2"  },
            new User {ID=Guid.NewGuid(),Username="user3",Password = "password", Fname="user3", Lname="Lname3"  }
        };

    }
}