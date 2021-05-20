package jpql;

import jpql.domain.Member;
import jpql.domain.MemberDTO;
import jpql.domain.Team;

import javax.persistence.*;
import java.util.List;

public class jpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        tx.begin();

        try {

            Team team = new Team();
            team.setTeamName("team1");
            em.persist(team);

            Member member = new Member();
            member.setUsername("member1");
            member.setAge(18);
            member.changeTeam(team);
            em.persist(member);

            em.flush();
            em.clear();

//            Member foundMember = getMember(em);
//            List<Member> foundMembers = getMembers(em);
//            List<MemberDTO> foundMembers2 = getMembersWithDTO(em);
//            List<Member> foundPagedMembers = getMembersWithPaging(em);
//            List<Member> result = getMembersWithJoins(em);

            List<Member> result = getMembersWithSubQuery(em);

            for (Member m : result) {
                System.out.println("member = " + m);
            }

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

    /**
     * Read all members with joins.
     * <p>1. Inner join.</p>
     * <p>2. Outer join.</p>
     * <p>3. Theta join.</p>
     * <p>3. Relational/Non Relational join.</p>
     */
    private static List<Member> getMembersWithJoins(EntityManager em) {
        // note. Inner join.
        String query = "select m from Member m inner join m.team t";

        // note. Outer join (left).
        query = "select m from Member m left join m.team t";

        // note. Theta join.
        query = "select m from Member m, Team t where m.username = t.teamName";

        // note. Relational join.
        query = "select m from Member m left join m.team t on t.teamName = 'team1'";

        // note. Non relational join.
        query = "select m from Member m left join Team t on m.username = t.teamName";

        List<Member> result = em.createQuery(query, Member.class)
                .getResultList();

        System.out.println("size: " + result.size());
        return result;
    }

    /**
     * Read all members with sub-query.
     */
    private static List<Member> getMembersWithSubQuery(EntityManager em) {
        // note. Sub query;
        String subQuery = "select m from Member m where m.age > (select avg(m2.age) from Member m2)";

        // note. Scalar query
        subQuery = "select (select avg(m2.age) from Member m2) from Member m";

        TypedQuery<Member> query = em.createQuery(subQuery, Member.class);

        return query.getResultList();
    }

}
