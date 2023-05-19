package com.orange.cliper.querymodel;

public class InMemoryPartiesEventHandlerUnitTest extends AbstractPartiesEventHandlerUnitTest {

    @Override
    protected PartiesEventHandler getHandler() {
        return new InMemoryPartiesEventHandler(emitter);
    }
}
