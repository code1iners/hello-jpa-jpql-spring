package jpql;

import jpql.domain.Member;
import jpql.domain.MemberDTO;
import jpql.domain.MemberType;
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

            Member member = new Member();
            member.setUsername("Admin");
            em.persist(member);

            Member member2 = new Member();
            member2.setUsername("User");
            em.persist(member2);

            em.flush();
            em.clear();

//            Member result = getMember(em);
//            List<Member> result = getMembers(em);
//            List<MemberDTO> result = getMembersWithDTO(em);
//            List<Member> result = getMembersWithPaging(em);
//            List<Member> result = getMembersWithJoins(em);
//            List<Member> result = getMembersWithSubQuery(em);
//            List<Object[]> result = getMembersWithTypes(em);
//            List<String> result = getMembersWithConditions(em);
//            List<String> result = getMembersWithCoalesce(em);
//            List<String> result = getMembersWithNullIf(em);
//            getMembersWithBasicFunction(em);
            getMembersWithCustomFunction(em);

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

    /**
     * Read all members with variety types.
     * <p>1. String.</p>
     * <p>2. Boolean.</p>
     * <p>3. Number. - 10L(Long) 10D(Double) 10F(Float)</p>
     */
    private static List<Object[]> getMembersWithTypes(EntityManager em) {
        String query = "select m.username, 'HELLO', TRUE from Member m ";
        query += "where m.type = :userType";
        Query resultQuery = em.createQuery(query)
                .setParameter("userType", MemberType.ADMIN);
        return resultQuery.getResultList();
    }

    /**
     * Read all members with case.
     */
    private static List<String> getMembersWithConditions(EntityManager em) {
        String query =
                "select " +
                        "  case when m.age <= 10 then 'Student fare' " +
                        "       when m.age >= 60 then 'Senior fare' " +
                        "       else 'Normal fare' " +
                        "   end" +
                        "  from Member m";

        String query2 =
                "select " +
                        "  case t.teamName " +
                        "       when 'team1' then 'Incentive 110%' " +
                        "       when 'team2' then 'Incentive 120%' " +
                        "       else 'Incentive 105%' " +
                        "   end" +
                        "  from Team t";

        List<String> result = em.createQuery(query2, String.class)
                .getResultList();

        for (String item : result) {
            System.out.println("item = " + item);
        }

        return result;
    }

    /**
     * Read all members with Coalesce.
     * <p>coalesce(param1, param2)</p>
     * <p>if first param1 is null, then return param2 as value.</p>
     */
    private static List<String> getMembersWithCoalesce(EntityManager em) {
        String query = "select coalesce(m.username, 'Unknown user') from Member m ";
        List<String> result = em.createQuery(query, String.class).getResultList();
        for (String s : result) {
            System.out.println("s = " + s);
        }

        return result;
    }

    /**
     * Read all members with null if.
     * <p>nullif(param1, param2)</p>
     * <p>if param1 is same with param2 then, return null value as result.</p>
     */
    private static List<String> getMembersWithNullIf(EntityManager em) {
        String query = "select nullif(m.username, 'Admin') from Member m ";
        List<String> result = em.createQuery(query, String.class).getResultList();
        for (String s : result) {
            System.out.println("s = " + s);
        }

        return result;
    }

    /**
     * Read all members with JPQL basic functions.
     * <p>CONCAT.</p>
     * <p>SUBSTRING.</p>
     * <p>TRIM.</p>
     * <p>LOWER, UPPER.</p>
     * <p>LENGTH.</p>
     * <p>LOCATE.</p>
     * <p>ABS, SQRT, MOD.</p>
     * <p>SIZE, INDEX(for JPA).</p>
     */
    private static void getMembersWithBasicFunction(EntityManager em) {
        // note. Concat. (return type String)
        String query = "select concat('a', 'b') from Member m ";
//        query = "select 'a' || 'b' from Member m "; // note. Same concat('a', 'b')

        // note. Substring. (return type String)
        query = "select substring(m.username, 2, 3) from Member m ";

        // note. Locate. (return type Integer)
        query = "select locate('de', 'abcdef') from Member m "; // note. return 4

        // note. Size(return type Integer)
        query = "select size(t.members) from Team t ";

        // note. Index (Not recommended)
//        query = "select index(t.members) From Team t ";

        List<Integer> result = em.createQuery(query, Integer.class)
                .getResultList();

        for (Integer s : result) {
            System.out.println("s = " + s);
        }
    }

    /**
     * Read all members with User custom defined functions.
     * <p>First, need jpql.dialect setting which database.</p>
     */
    private static void getMembersWithCustomFunction(EntityManager em) {
        // note. Usage user custom defined function.
        String query = "select function('group_concat', m.username) from Member m ";
//        query = "select group_concat(m.username) from Member m"; // note. Same above query.
        List<String> result = em.createQuery(query, String.class)
                .getResultList();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }
}
