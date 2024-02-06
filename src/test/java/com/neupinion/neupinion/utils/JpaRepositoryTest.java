package com.neupinion.neupinion.utils;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public abstract class JpaRepositoryTest {

    @Autowired
    protected EntityManager entityManager;

    protected void saveAndClearEntityManager() {
        entityManager.flush();
        entityManager.clear();
    }
}
