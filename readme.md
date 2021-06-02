# FS4R - File System for Remote <small>access</small>

A [DSR](https://ru.dsr-corporation.com/) practise project.

### Description

This project is a server application that provides file system and operations over it. Basically its RMCD _(Read, Move,
Copy, Delete)_ service for files and directories.

#### Structure

Project is separated on `core` and `service` modules:

- `core` - implements main operations over paths (files and directories). Additional feature is `ConcurrentIo` that
  provides interaction with program-wide concurrency.
- `service` - spring based RESTlike service. Extends `core` module with `virtual_fs` package provides encapsulation of
  server file system via publish specific paths.

### How to start

1. Clone project code sources into some directory (for example let it be `your_fs4r_dir`).
2. Open `your_fs4r_dir` as gradle based project in your favorite IDE.
3. Verify code by running gradle `check` task. Alternatively you can achieve it by running `test` task
   from `your_fs4r_dir/build.gradle`.

### Run

#### Bootable Jar

1. Run `bootJar` task from `your_fs4r_dir/service/build.gradle`.
2. Built jar file is stored in `your_fs4r_dir/service/build/libs` directory with name like `fs4r-service-[VERSION].jar`
3. Run it with command `java -jar fs4r-service-[VERSION].jar`. Append it with required arguments like path to resources
   or active profiles.

#### Run docker container from GitHub Container registry

This article will be added in the future.

#### Build your own docker container

1. To create Docker image run `docker build .` command in `your_fs4r_dir` directory. Image name is `fs4r-service`.
2. Run image with command `docker run -p [PORT]:8080 fs4r-service`. Append it with required arguments after
   keyword `ARG`.

### Run configuration

#### Run Profiles

You may specify service behavior with profiles, pass them as `spring.profiles.active` property:

- `concurrent-sessions` - enables extended concurrency support with stamp based locks. Read more
  in [Concurrency support](#Concurrency support) section.

#### Application properties

Service supports Spring properties (`server.port`, `logging.level`, etc.).

#### Service specific application properties

- `fs4r.publish-dirs` - service publish paths. If passed more than one path, service creates _virtual_ root directory
  that wraps published files.
- `fs4r.web.allowed-origins` - specifies origins allowed interacting with
  service. [CORS](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS)
- `fs4r.load-files-size-limit` - specifies file size limit (in bytes) for loading via `/main/load/**` endpoint
- `fs4r.security.reader-password` - specifies password for user with `READ` authority.
  (default: _{bcrypt}$2y$12$4z8y0T6R.5aYu7HpqzPkE.pQF9twkbeSHnY5UoOEDMtKCbh0KPJ4q_ ~ 123)
- `fs4r.security.regular-password` - specifies password for user with `READ`/`WRITE` authorities.
  (default: _{bcrypt}$2y$12$5GBgieTpZsK5ASKWSlS9T.ef0ZdUlR6mLv0aRZSobQ.FtsmdwVyCa_ ~ 321)

### Concurrency support

Service provides two levels of concurrency:

1. [Thread level](#Threads concurrency support) - is activated in any case without being tied to a configuration.
2. [Request level](#Sessions concurrency support) - is activated by profile `concurrent-sessions`.

#### Threads concurrency support

Basic concurrency support. When some thread tries to operate over _real_ path, it requests read/write lock on that path.

#### Sessions concurrency support

Extended concurrency support. Is enabled with profile `concurrent-sessions`, and provides concurrency support for
request/sessions via stamp based locks on _virtual_ paths.  
When user wants to guard some _virtual_ paths, it requests exclusive/concurrent lock over that paths. Exclusive lock
guarantees that no other user will be allowed to read and modify target path. Concurrent lock allows other user to read
target path and request concurrent lock on it, but prevents exclusive locking and modifying.

### Contributors

- Valentine Mazurov - _code, bugs, etc_
- Александр Мартынов - _DSR practice mentor_
