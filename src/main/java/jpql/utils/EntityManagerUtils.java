package jpql.utils;

import javax.persistence.EntityManager;

public class EntityManagerUtils {
    public static void clearAndPrintLine (EntityManager em, String comment) {
        em.clear();
        System.out.println("\n======= END ======= " + comment + " ======= END =======\n");
    }
}
