---
description: Migrate a feature package to the layered convention (controller/service+impl/repository/entity/dto/mapper), piloted on attendance & roles
argument-hint: <module-package-name, e.g. wages>
---

Migrate `net.ravik_cms.ravik_backend.$ARGUMENTS` from a flat feature package to the
layered convention established during the attendance/roles pilot
(see `/Users/munene/.claude/plans/noble-herding-russell.md` for the original rationale
and verification results — read it once for context, then follow the steps below for
this module). If `$ARGUMENTS` is empty, list the remaining un-migrated feature packages
under `src/main/java/net/ravik_cms/ravik_backend/` and ask which one to do next.

## Target layout

```
<module>/
  controller/   — @RestController classes
  service/      — one interface per service, same name as today's class
  service/impl/ — implementation, named <Name>ServiceImpl, same @Service + @RequiredArgsConstructor logic
  repository/   — Spring Data JPA repository interfaces
  entity/       — @Entity classes
  dto/          — request/response DTOs and JPQL projection records/interfaces
  mapper/       — MapStruct @Mapper(componentModel = "spring") interfaces
```

Only create the subpackages this module actually needs (e.g. skip `mapper/` if there's
no MapStruct mapper).

## Steps

1. **Survey the module.** List every file directly under the module's top-level package
   and classify each: entity, controller, service (concrete class today), repository,
   mapper, or DTO/projection.

2. **Move files with `git mv`**, one per classification, into the matching subpackage.
   Then update each moved file's `package` declaration to match its new path.

3. **Split the service.**
   - Create `service/<Name>Service.java` as an interface, with the exact same public
     method signatures as the current concrete class (keep the same interface name the
     class has today — callers' field types don't need to change).
   - Rename the current class to `<Name>ServiceImpl`, move it to `service/impl/`, make
     it `implement <Name>Service`, and add `@Override` on every method the interface
     declares (skip private helper methods).

4. **Fix intra-module references.** Any class in the module that referenced a sibling
   class by simple name (legal before, since they shared one package) now needs an
   explicit `import` pointing at the sibling's new subpackage.

5. **Fix cross-module consumers.** Grep the whole `src/main/java` tree for
   `net.ravik_cms.ravik_backend.$ARGUMENTS.<OldSimpleClassName>` imports — every class
   outside this module that imports something from it. Update each import line to the
   new subpackage path. The simple class name is unchanged (services keep their
   interface name), so only the import statement changes, never field types or call
   sites.

6. **Hunt for string-literal package references — these are NOT caught by import
   fixes or the compiler.** Specifically:
   - `@Query` JPQL/native queries with a fully-qualified constructor expression like
     `new net.ravik_cms.ravik_backend.$ARGUMENTS.SomeProjection(...)` — update the FQN
     to include the new `dto` (or wherever the projection landed) segment.
   - Any other fully-qualified class name inside a string (SpEL expressions,
     `@Query(nativeQuery=...)`, reflection, exception messages that happen to embed a
     package path) — grep for `net.ravik_cms.ravik_backend.$ARGUMENTS.` inside `.java`
     files to catch these; a plain import-statement grep will miss them since they're
     string literals.

7. **Housekeeping.** If you find an empty/unused interface or class while doing this
   (e.g. a MapStruct mapper with no mapping methods and no injection points), flag it to
   the user rather than silently deleting or silently keeping it — mention it in your
   summary as a candidate for removal.

## Verification (do not skip)

1. `./mvnw -q install -f pom.xml` — must compile and the `contextLoads` test must pass.
   A new compile error or Hibernate startup failure means an import or a string-literal
   FQN was missed.
2. Start the app (`./mvnw -q spring-boot:run -f pom.xml`, or reuse a running instance)
   and confirm the module's endpoints are still registered via
   `curl -s http://localhost:8080/v3/api-docs | jq '.paths | keys[] | select(contains("/api/v1/<module-path>"))'`
   (403 for unauthenticated calls is fine — that means the route resolved; 404 means the
   controller mapping broke).
3. If the module has any `@Query` with a projection constructor expression or other
   runtime-only string-literal reference, write a throwaway `@SpringBootTest` that
   autowires the repository and calls that query method with dummy arguments, run it
   with `./mvnw -q test -Dtest=<TestName> -f pom.xml` to confirm it actually resolves at
   runtime, then delete the test — don't leave scratch tests in the tree.
4. `git status --short` — the diff should be limited to this module's package plus the
   cross-module consumer files found in step 5. A wider diff signals a missed reference.

## Report

Summarize: files moved, the new service interface/impl pair, cross-module files
touched, any string-literal fixes made, and the verification results. Flag anything
from step 7 (housekeeping candidates) explicitly rather than folding it silently into
the diff.
