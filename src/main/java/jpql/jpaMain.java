package jpql;

import jpql.domain.Member;
import jpql.domain.MemberDTO;

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

            Member foundMember = getMember(em);
            List<Member> foundMembers = getMembers(em);

            List<MemberDTO> foundMembers2 = getMembersWithDTO(em);

            for (MemberDTO m : foundMembers2) {
                System.out.println("member = " + m);
            }

            Member selectedMember = foundMembers.get(0);
            selectedMember.setAge(10);

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
    }

    private static Member getMember(EntityManager em) {
        return em
                .createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username", "member1")
                .getSingleResult();
    }

    private static List<Member> getMembers(EntityManager em) {
        return em
                .createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    private static List<MemberDTO> getMembersWithDTO(EntityManager em) {
        return em.createQuery("select new jpql.domain.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
                .getResultList();
    }

}
