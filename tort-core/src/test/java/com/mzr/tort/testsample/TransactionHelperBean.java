package com.mzr.tort.testsample;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TransactionHelperBean {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void doInTransaction(Runnable runnable) {
        runnable.run();
    }

}
