# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.1.1] - 2026-10-26

Security patch release. Dependency updates only; no functional changes.

### Security

- **Thymeleaf** `3.1.2.RELEASE` → `3.1.5.RELEASE` (`thymeleaf`, `thymeleaf-spring6`)
  - [CVE-2026-40477](https://nvd.nist.gov/vuln/detail/CVE-2026-40477) (Critical) — expression sandbox bypass that can lead to server-side template injection
  - Spring Boot 3.2.5 manages Thymeleaf 3.1.2; the version is now overridden via the `thymeleaf.version` property.
- **Jackson** `2.17.2` / `2.15.4` → `2.21.8` (all modules)
  - `jackson-databind`:
    [CVE-2026-54512](https://nvd.nist.gov/vuln/detail/CVE-2026-54512),
    [CVE-2026-54513](https://nvd.nist.gov/vuln/detail/CVE-2026-54513),
    [CVE-2026-68497](https://nvd.nist.gov/vuln/detail/CVE-2026-68497),
    [CVE-2026-91776](https://nvd.nist.gov/vuln/detail/CVE-2026-91776),
    [CVE-2026-91777](https://nvd.nist.gov/vuln/detail/CVE-2026-91777) (High);
    [CVE-2026-19032](https://nvd.nist.gov/vuln/detail/CVE-2026-19032),
    [CVE-2026-54514](https://nvd.nist.gov/vuln/detail/CVE-2026-54514),
    [CVE-2026-54515](https://nvd.nist.gov/vuln/detail/CVE-2026-54515),
    [CVE-2026-59888](https://nvd.nist.gov/vuln/detail/CVE-2026-59888),
    [CVE-2026-59889](https://nvd.nist.gov/vuln/detail/CVE-2026-59889),
    [CVE-2026-77310](https://nvd.nist.gov/vuln/detail/CVE-2026-77310),
    [CVE-2026-83557](https://nvd.nist.gov/vuln/detail/CVE-2026-83557) (Moderate)
  - `jackson-core`:
    [CVE-2026-68494](https://nvd.nist.gov/vuln/detail/CVE-2026-68494),
    [CVE-2026-89407](https://nvd.nist.gov/vuln/detail/CVE-2026-89407),
    [CVE-2026-89425](https://nvd.nist.gov/vuln/detail/CVE-2026-89425) (High);
    [CVE-2026-18401](https://nvd.nist.gov/vuln/detail/CVE-2026-18401) (Moderate)

### Changed

- Jackson versions are now set in one place via the `jackson-bom.version` property. The explicit
  version pins on `jackson-databind`, `jackson-core` and `jackson-datatype-jsr310` were removed, so all
  Jackson modules (including `jackson-annotations`, `jackson-datatype-jdk8` and
  `jackson-module-parameter-names`, previously left at 2.15.4) resolve to the same release.

[1.1.1]: https://github.com/SolaceLabs/ep-asyncapi-sap-is-converter/compare/v1.1.0...v1.1.1
