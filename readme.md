# FS4R - File System for Remote <small>access</small>

A [DSR](https://ru.dsr-corporation.com/) practise project.

### Description

This project is a server application that provides file system and operations over it. Basically its RMCD _(Read, Move,
Copy, Delete)_ service for files and directories.

### How to start

1. Clone project code sources into some directory (for example let it be `your_fs4r_dir`).
2. Open `your_fs4r_dir` as gradle based project in your favorite IDE.
3. Proceed unit tests by running `test` task from `your_fs4r_dir/build.gradle`.

### Run

#### Bootable Jar

1. Run `bootJar` task from `your_fs4r_dir/service/build.gradle`.
2. Built jar file is stored in `your_fs4r_dir/service/build/libs` directory with name like `fs4r-service-[VERSION].jar`
3. Run it with command `java -jar fs4r-service-[VERSION].jar`. Append it with required arguments like path to resources
   or active profiles.

#### Docker container

1. To create Docker image run `docker build .` command in `your_fs4r_dir` directory. Image name is `fs4r-service`.
2. Run image with command `docker run -p [PORT]:8080 fs4r-service`. Append it with required arguments after
   keyword `ARG`.

### Run configuration

#### Run profiles

Service supports run profiles via passing `spring.profiles.active` property:

- `test` - provides no auth endpoints

#### Application properties

Service supports Spring properties (`server.port`, `logging.level`, etc.).

#### Service specific application properties

- `fs4r.root-dir` - service publish path. Used as _virtual_ root directory. Uses system's root if property not
  specified.

### Contributors

- Valentine Mazurov - _code, bugs, etc_
- Александр Мартынов - _DSR practice mentor_
