package jpql;

import jpql.domain.Member;
import jpql.domain.MemberDTO;
import jpql.domain.MemberType;
import jpql.domain.Team;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

import static jpql.utils.EntityManagerUtils.clearAndPrintLine;

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

            Team team2 = new Team();
            team2.setTeamName("team2");
            em.persist(team2);

            Member admin = new Member();
            admin.setUsername("admin");
            admin.setTeam(team);
            admin.setAge(40);
            em.persist(admin);

            Member user = new Member();
            user.setUsername("user");
            user.setTeam(team2);
            user.setAge(18);
            em.persist(user);

            Member member1 = new Member();
            member1.setUsername("member1");
            member1.setAge(20);
            member1.setTeam(team);
            em.persist(member1);

            Member member2 = new Member();
            member2.setUsername("member2");
            member2.setAge(22);
            member2.setTeam(team2);
            em.persist(member2);

            Member member3 = new Member();
            member3.setUsername("member3");
            member3.setTeam(team);
            member3.setAge(30);
            em.persist(member3);

//            em.flush();
//            em.clear();

//            dynamicallyParameter(em);
//            selectListData(em);
//            domainDto(em);
//            pagingAndSort(em);
//            joins(em);
//            subQuery(em);
//            types(em);
//            conditionalExpression(em);
//            coalesce(em);
//            nullIf(em);
//            SQLFunctions(em);
//            customSQLFunction(em);
//            pathExpression(em);
//            fetchJoin(em);
//            namedQuery(em);
            bulkCalculate(em);

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
     * <h3>Select Single data with Dynamically parameters</h3>
     */
    private static void dynamicallyParameter(EntityManager em) {
        Team newTeam = new Team();
        newTeam.setTeamName("team1");
        em.persist(newTeam);

        Member newMember = new Member();
        newMember.setUsername("member1");
        newMember.setTeam(newTeam);
        em.persist(newMember);

        String query = "select m from Member m where m.username = :username";
        Member result = em.createQuery(query, Member.class)
                .setParameter("username", "member1")
                .getSingleResult();

        System.out.println("result = " + result);

        clearAndPrintLine(em, "Dynamically parameter");

        /**
         * if use entity directly. then, using pk of entity.
         */
        query = "select m from Member m where m = :member";
        Member result2 = em.createQuery(query, Member.class)
                .setParameter("member", newMember)
                .getSingleResult();

        System.out.println("result2 = " + result2);

        clearAndPrintLine(em, "Used entity directly");

        /**
         * If use entity pk then, same above result.
         */
        query = "select m from Member m where m.id = :memberId";
        result2 = em.createQuery(query, Member.class)
                .setParameter("memberId", newMember.getId())
                .getSingleResult();

        System.out.println("result2 = " + result2);

        clearAndPrintLine(em, "Used entity's pk");

        /**
         * If use entity fk.
         */
        query = "select m from Member m where m.team = :team";
        List result3 = em.createQuery(query)
                .setParameter("team", newTeam)
                .getResultList();

        for (Object o : result3) {
            System.out.println("o = " + o);
        }
        clearAndPrintLine(em, "Used fk entity directly");

        query = "select m from Member m where m.team.id = :teamId";
        result3 = em.createQuery(query)
                .setParameter("teamId", newTeam.getId())
                .getResultList();

        for (Object o : result3) {
            System.out.println("o = " + o);
        }
        clearAndPrintLine(em, "Used fk entity's id");
    }

    /**
     * <h3>Select List data</h3>
     */
    private static void selectListData(EntityManager em) {
        List<Member> result = em
                .createQuery("select m from Member m", Member.class)
                .getResultList();

        for (Member member : result) {
            System.out.println("member = " + member.getUsername());
        }
    }

    /**
     * <h3>Domain DTO</h3>
     */
    private static void domainDto(EntityManager em) {
        List<MemberDTO> result = em.createQuery("select new jpql.domain.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
                .getResultList();

        for (MemberDTO memberDTO : result) {
            System.out.println("memberDTO = " + memberDTO.getUsername());
        }
    }

    /**
     * <h3>Paging & Sort</h3>
     */
    private static void pagingAndSort(EntityManager em) {
        List<Member> result = em.createQuery("select m from Member m order by m.age ", Member.class)
                .setFirstResult(1)
                .setMaxResults(10)
                .getResultList();

        for (Member member : result) {
            System.out.println("member.getUsername() = " + member.getUsername());
        }
    }

    /**
     * <h3>Joins</h3>
     * <p>1. Inner join.</p>
     * <p>2. Outer join.</p>
     * <p>3. Theta join.</p>
     * <p>3. Relational/Non Relational join.</p>
     */
    private static void joins(EntityManager em) {
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
        for (Member member : result) {
            System.out.println("member = " + member.getUsername());
        }
    }

    /**
     * <h3>Sub query</h3>
     */
    private static void subQuery(EntityManager em) {
        // note. Sub query;
        String query = "select m from Member m where m.age > (select avg(m2.age) from Member m2)";

        List<Member> result = em.createQuery(query, Member.class)
                .getResultList();

        for (Member member : result) {
            System.out.println("member.getUsername() = " + member.getUsername());
        }
        clearAndPrintLine(em, "Sub query");

        // note. Scalar query
        query = "select (select avg(m2.age) from Member m2) from Member m";
        List<Double> result2 = em.createQuery(query, Double.class)
                .getResultList();

        for (Double avg : result2) {
            System.out.println("avg = " + avg);
        }
        clearAndPrintLine(em, "Scalar query");

    }

    /**
     * <h3>Types</h3>
     * <p>1. String.</p>
     * <p>2. Boolean.</p>
     * <p>3. Number. - 10L(Long) 10D(Double) 10F(Float)</p>
     */
    private static void types(EntityManager em) {
        String query = "select m.username, 'HELLO', TRUE from Member m ";
        query += "where m.type = :userType";
        List result = em.createQuery(query)
                .setParameter("userType", MemberType.ADMIN)
                .getResultList();

        for (Object o : result) {
            System.out.println("o = " + o);
        }
    }

    /**
     * <h3>Conditional expression</h3>
     */
    private static void conditionalExpression(EntityManager em) {
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
    }

    /**
     * <h3>Coalesce</h3>
     * <p>coalesce(param1, param2)</p>
     * <p>if first param1 is null, then return param2 as value.</p>
     */
    private static void coalesce(EntityManager em) {
        String query = "select coalesce(m.username, 'Unknown user') from Member m ";
        List<String> result = em.createQuery(query, String.class).getResultList();
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    /**
     * <h3>Nullif</h3>
     * <p>nullif(param1, param2)</p>
     * <p>if param1 is same with param2 then, return null value as result.</p>
     */
    private static void nullIf(EntityManager em) {
        String query = "select nullif(m.username, 'admin') from Member m ";
        List<String> result = em.createQuery(query, String.class).getResultList();
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    /**
     * <h3>SQL functions</h3>
     * <p>CONCAT.</p>
     * <p>SUBSTRING.</p>
     * <p>TRIM.</p>
     * <p>LOWER, UPPER.</p>
     * <p>LENGTH.</p>
     * <p>LOCATE.</p>
     * <p>ABS, SQRT, MOD.</p>
     * <p>SIZE, INDEX(for JPA).</p>
     */
    private static void SQLFunctions(EntityManager em) {
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
     * <h3>Custom SQL function</h3>
     * <p>First, need jpql.dialect setting which database.</p>
     */
    private static void customSQLFunction(EntityManager em) {
        // note. Usage user custom defined function.
        String query = "select function('group_concat', m.username) from Member m ";
//        query = "select group_concat(m.username) from Member m"; // note. Same above query.
        List<String> result = em.createQuery(query, String.class)
                .getResultList();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    /**
     * <h3>Path expressions.</h3>
     * <p>1. Status field : A field for simply storing values.</p>
     * <p>2. Single association field : A field for association with Entity (@ManyToOne @OneToOne).</p>
     * <p>3. Collection association field : A field for association with Collection (@OneToMany @ManyToMany).</p>
     * <br/>
     * <h4>Example)</h4>
     *     select m.username -> <b>Status field.</b><br/>
     *       from Member m<br/>
     *     join m.team t -> <b>Single association field.</b><br/>
     *     join m.orders o -> <b>Collection association field.</b><br/>
     *     where t.name = 'team1'<br/>
     */
    private static void pathExpression(EntityManager em) {
        /**
         * <h2>Status field.</h2>
         */
        String query = "select m.username from Member m";

        TypedQuery<String> statusFieldQuery = em.createQuery(query, String.class);
        List<String> statusFieldResult = statusFieldQuery.getResultList();

        for (String username : statusFieldResult) {
            System.out.println("username = " + username);
        }

        clearAndPrintLine(em, "Status field");

        /**
         * Single association field.
         * 1. Include implied join query.
         * 2. Recommended what write explicit join query.
         */
        query = "select m.team.teamName from Member m";

        TypedQuery<String> teamNameQuery = em.createQuery(query, String.class);
        List<String> teamNameResult = teamNameQuery.getResultList();

        for (String teamName : teamNameResult) {
            System.out.println("teamName = " + teamName);
        }

        clearAndPrintLine(em, "Single association field");

        /**
         * Collection association field.
         * 1. Include implied join query.
         * 2. Recommended what write explicit join query.
         */
        query = "select t.members from Team t";

        TypedQuery<Collection> teamQuery = em.createQuery(query, Collection.class);
        List<Collection> teamResult = teamQuery.getResultList();

        for (Object team : teamResult) {
            System.out.println("team = " + team);
        }

        clearAndPrintLine(em, "Collection association field with Implied join query");

        /**
         * Collection association field with Explicit join query.
         */
         query = "select m.username from Team t join t.members m";
        TypedQuery<String> explicitQuery = em.createQuery(query, String.class);
        List<String> explicitResult = explicitQuery.getResultList();

        for (String username : explicitResult) {
            System.out.println("username = " + username);
        }

        clearAndPrintLine(em, "Collection association field with Explicit join query");
    }

    /**
     * <h3>Fetch join</h3>
     * <p>This is not a SQL join.</p>
     * <p>Using for optimize performance.</p>
     * <p>Getting data related to an entity or collection in one go.</p>
     * <p>Usage : join fetch</p>
     * <p>Do not use alias in Fetch join.</p>
     * <p>Do not use Fetch join on more than one collection.</p>
     * <p>Not recommended use paging API when used fetch join with collection.</p>
     */
    private static void fetchJoin(EntityManager em) {
        /**
         * Not using fetch join.
         * Call query 3 times.
         * It raises the n + 1 issue.
         */
        String query = "select m from Member m ";
        List<Member> result = em.createQuery(query, Member.class)
                .getResultList();

        for (Member member : result) {
            System.out.println("member = " + member.getUsername() + ", " + member.getTeam().getTeamName());
        }

        clearAndPrintLine(em, "Not using fetch join");

        /**
         * Using fetch join.
         * Call query 1 time.
         * Recommended way.
         */
        query = "select m from Member m join fetch m.team";
        result = em.createQuery(query, Member.class)
                .getResultList();

        for (Member member : result) {
            System.out.println("member = " + member.getUsername() + ", " + member.getTeam().getTeamName());
        }

        clearAndPrintLine(em, "Using fecth join");

        /**
         * Collection fetch join.
         * There may be duplicate data.
         */
        query = "select t from Team t join fetch t.members";
        List<Team> teamResult = em.createQuery(query, Team.class).getResultList();

        System.out.println("teamResult = " + teamResult.size());
        for (Team team : teamResult) {
            System.out.println("team = " + team.getTeamName() + ", members size = " + team.getMembers().size());
            for (Member member : team.getMembers()) {
                System.out.println("member = " + member.getUsername() + ", " + member.getTeam().getTeamName());
            }
        }

        clearAndPrintLine(em, "Collection fetch join");

        /**
         * Collection fetch join with distinct.
         * Removed which duplicate data in SQL.
         * Removed which duplicate data in Application entity.
         */
        query = "select distinct t from Team t join fetch t.members";
        teamResult = em.createQuery(query, Team.class)
                .getResultList();

        System.out.println("teamResult = " + teamResult.size());
        for (Team team : teamResult) {
            System.out.println("team = " + team.getTeamName() + ", members size = " + team.getMembers().size());
            for (Member member : team.getMembers()) {
                System.out.println("member = " + member.getUsername() + ", " + member.getTeam().getTeamName());
            }
        }

        clearAndPrintLine(em, "Collection fetch join with distinct");
    }

    private static void namedQuery(EntityManager em) {
        Member member = new Member();
        member.setUsername("member1");
        em.persist(member);

        List<Member> result = em.createNamedQuery("Member.findByUsername", Member.class)
                .setParameter("username", "member1")
                .getResultList();

        for (Member m : result) {
            System.out.println("m.getUsername() = " + m.getUsername());
        }

        clearAndPrintLine(em, "Named query");
    }

    /**
     * <h3>Bulk calculate</h3>
     * <p>It's supports calculate Large amounts of data (update, delete).</p>
     * <p>It's supports insert -> insert into .. select (only hibernate).</p>
     * <p>Ignore all Persistence context.</p>
     * <p>Tip 1 : Doing bulk calculation first.</p>
     * <p>Tip 2 : Initialize Persistence context after bulk calculate.</p>
     */
    private static void bulkCalculate(EntityManager em) {
        Member newMember = new Member();
        newMember.setUsername("bulk");
        newMember.setAge(24);
        em.persist(newMember);

        String query = "update Member m set m.age = 20";
        int resultCount = em.createQuery(query)
                .executeUpdate();

        System.out.println("resultCount = " + resultCount);

        /**
         * Applied update in database.
         * But non applied in persistence context.
         * See below code results.
         */
        query = "select m from Member m";
        List<Member> result = em.createQuery(query, Member.class).getResultList();
        for (Member member : result) {
            System.out.println("member.getUsername() = " + member.getUsername() + ", age = " + member.getAge());
        }

        Member foundMember = em.find(Member.class, newMember.getId());
        System.out.println("foundMember = " + foundMember);

        clearAndPrintLine(em, "Bulk calculate before persistence clear");

        /**
         * Entity manager is cleared.
         */
        result = em.createQuery(query, Member.class).getResultList();
        for (Member member : result) {
            System.out.println("member.getUsername() = " + member.getUsername() + ", age = " + member.getAge());
        }

        foundMember = em.find(Member.class, newMember.getId());
        System.out.println("foundMember = " + foundMember);

        clearAndPrintLine(em, "Bulk calculate after persistence clear");

        // note. See Tip 2.
//        em.clear();
    }
}
