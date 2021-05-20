package jpql;

import jpql.domain.Member;

import javax.persistence.*;
import java.util.List;

public class jpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {
            Member member = new Member();
            member.setUsername("member1");
            member.setAge(19);
            em.persist(member);

            em.flush();
            em.clear();

            Member singleResult = em
                    .createQuery("select m from Member m where m.username = :username", Member.class)
                    .setParameter("username", "member1")
                    .getSingleResult();


            System.out.println("singleResult = " + singleResult.getUsername());
//            for (Member m : members) {
//                System.out.println("m = " + m);
//            }

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }
}
