package org.example.users.repository;



import jakarta.transaction.Transactional;
import org.example.users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    @Query(value = "SELECT * FROM dbmaster.user cc WHERE cc.user_mail =:user_mail AND cc.user_password=:password" ,nativeQuery = true)
    User getUserByPassAndName(String user_mail, String password);

    @Query(value = "SELECT * FROM dbmaster.user WHERE user_mail=:user_mail" ,nativeQuery = true)
    User getUserByMail(String user_mail);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM dbmaster.user WHERE user_mail =:user_mail" ,nativeQuery = true)
    int deleteUser(String user_mail);


}
