package com.ddd.architecture;

import java.util.UUID;

@Port
public interface Command {
   UUID aggregateId();
}
