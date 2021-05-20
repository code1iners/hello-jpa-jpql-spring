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
            for (int i=0; i<100; i++) {
                Member member = new Member();
                member.setUsername("member" + i);
                member.setAge(i);
                em.persist(member);
            }

            em.flush();
            em.clear();

            Member foundMember = getMember(em);
            List<Member> foundMembers = getMembers(em);
            List<MemberDTO> foundMembers2 = getMembersWithDTO(em);
            List<Member> foundPagedMembers = getMembersWithPaging(em);


            for (Member m : foundPagedMembers) {
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

    /**
     * Read just one member with parameter.
     */
    private static Member getMember(EntityManager em) {
        return em
                .createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username", "member1")
                .getSingleResult();
    }

    /**
     * Read all members simply.
     */
    private static List<Member> getMembers(EntityManager em) {
        return em
                .createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    /**
     * Read all members with domain DTO.
     */
    private static List<MemberDTO> getMembersWithDTO(EntityManager em) {
        return em.createQuery("select new jpql.domain.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
                .getResultList();
    }

    /**
     * Read all members with paging and sort.
     */
    private static List<Member> getMembersWithPaging(EntityManager em) {
        return em.createQuery("select m from Member m order by m.age ", Member.class)
                .setFirstResult(1)
                .setMaxResults(10)
                .getResultList();
    }

}
