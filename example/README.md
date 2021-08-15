# DDD Example

Using the requirements from the article:
https://spring.io/blog/2018/04/11/event-storming-and-spring-with-a-splash-of-ddd


Business requirements:

```
Assign initial limit to a card
Withdraw money
Create a statement with amount of money to be repaid (at the end of a billing cycle)
Repay money
Order or change personalized plastic card
```

Technical requirements:

```
Code implemented with Java 16 features
Code implemented with Onion Architecture in mind
```

## How to test

```java
mvn clean test jacoco:report
```


## Bounded Contexts

![](../docs/images/es-example-bounded-contests.png)

**Card Operations:**

- [x] Withdraw
- [x] Limit
- [ ] Repay

![](../docs/images/jmolecules.png)

![](../docs/images/jmolecules2.png)

---

https://www.archunit.org/userguide/html/000_Index.html#_onion_architecture

In an "Onion Architecture" (also known as "Hexagonal Architecture" or "Ports and Adapters"), we can define domain packages and adapter packages as follows.

```
onionArchitecture()
        .domainModels("com.myapp.domain.model..")
        .domainServices("com.myapp.domain.service..")
        .applicationServices("com.myapp.application..")
        .adapter("cli", "com.myapp.adapter.cli..")
        .adapter("persistence", "com.myapp.adapter.persistence..")
        .adapter("rest", "com.myapp.adapter.rest..");
```

The semantic follows the descriptions in https://jeffreypalermo.com/2008/07/the-onion-architecture-part-1/. More precisely, the following holds:

- The domain package is the core of the application. It consists of two parts.
  - The domainModels packages contain the domain entities.
  - The packages in domainServices contains services that use the entities in the domainModel packages.
- The applicationServices packages contain services and configuration to run the application and use cases. It can use the items of the domain package but there must not be any dependency from the domain to the application packages.
- The adapter package contains logic to connect to external systems and/or infrastructure. No adapter may depend on another adapter. Adapters can use both the items of the domain as well as the application packages. Vice versa, neither the domain nor the application packages must contain dependencies on any adapter package.

