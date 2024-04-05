# bdrc-new-archive 
This `pom.xml` project contains two modules:
- OcflClient: A simple client that interacts with several OCFL layouts.
- RepoClient: A client that interacts with an fcrepo instance, either locally, or remote.

## OcflClient
This module defines a package, `io.bdrc.lib.OcflClient` which contains four main classes,
and supporting code. The main classes do the same work, but on three different repository
layouts.[^1] The class name is also the name of the Layout, which is used for:
* The name of the repository root directory
* The name of the subfolder of "sample/sample-output"  

[^1]: Storage layouts are defined in the [OCFL Community Extensions](https://ocfl.github.io/extensions/). Only some extensions
define storage layouts.

| Layout FolderName/MainClass name | Layout definition                                                                                          |
|----------------------------------|------------------------------------------------------------------------------------------------------------|
| RepoFlat                         | [0002: Flat Direct Storage Layout](https://ocfl.github.io/extensions/0002-flat-direct-storage-layout.html) |
| RepoNTupleOmitPrefix             | [0007: N Tuple Omit Prefix Storage Layout](https://ocfl.github.io/extensions/0007-n-tuple-omit-prefix-storage-layout.html)                                                                                                       |
| RepoNHash                        | [0004-hashed-n-tuple-storage-layout](https://ocfl.github.io/extensions/0004-hashed-n-tuple-storage-layout.html)         |

Each main class: 
- defines string constants
- re-creates a standalone repository, using one of the storage layout classes
- populates it from a sample work. It populates a **single repository object** that is a work's `archive``images``sources` and `other` directories.
- Gets the objects in the repository into a directory that corresponds to the 
repository root name (under `sample/sample-output`)

An additional main class, `SampleDemo` demonstrated editng the objects in a repository,
in such a way as to generate versioning.

### Generated Folder Structure

```shell
 tree -d -L 3 .
.
├── FlatLayout
│   ├── ocfl-repo
│   │   └── W1PD177852
│   └── ocfl-work
│   ├── ocfl-repo
│   │   ├── 17d
│   │   └── extensions
│   └── ocfl-work
├── RepoNTupleOmitPrefix
│   ├── ocfl-repo
│   ├── W1P
│   │  └── D17
│   │      └── 785
│   │          └── W1PD177852
│   │              └── v1
│   │                  └── content
│   │                      ├── I1KG18331
│   │                      │   └── archive
│   │                      ├── I1KG18332..I1KG18367
│   │                      │   └── archive
│   │  └── extensions
│   └── ocfl-work
├── SampleFlatLayout
│   ├── ocfl-repo
│   │   └── o1
│   └── ocfl-work
├── sample
│   ├── sample-dest
│   │   ├── object-in-dir
│   │   ├── object-in-dir-head
│   │   └── object-in-dir-v2
│   ├── sample-output
│   │   ├── FlatLayout
│   │   ├── RepoNHash
│   │   └── RepoNTupleOmitPrefix
│   ├── sample-source
│   │   └── dir1
│   ├── sample-updates
│   └── sample-work-source
│       ├── W1PD177852
│       └── dir1
```
The First 5 directories are the OCFL repositories that the code generated:
- FlatLayout
- RepoNHash
- RepoNTupleOmitPrefix
- SampleFlatLayout

```shell
├── FlatLayout
│   ├── ocfl-repo
│   │   └── W1PD177852
│   └── ocfl-work
├── RepoNHash
│   ├── ocfl-repo
│   │   ├── 17d
│   │   └── extensions
│   └── ocfl-work
├── RepoNTupleOmitPrefix
│   ├── ocfl-repo
│   │   ├── W1P
│   │   └── extensions
│   └── ocfl-work
├── SampleFlatLayout
│   ├── ocfl-repo
│   │   └── o1
│   └── ocfl-work
```

### Flat Layout
This layout faithfully represents the original internal structure of the source:  The work is a single directory,
with subdirectories for `archive`, `images`, `sources`, and `other`.[^2]

[^2] The input's `images` and `sources directory contain the same files as the `archive` directory, so the OCFL repository deduplicates them. When the repo is downloaded,
those duplicate trees reappear.

Notice that the object name appears in clear text. This makes for a very transparent file structure, but one that is unwieldy for large numbers of objects.


```shell
❯ tree -d -L 6 FlatLayout
FlatLayout
├── ocfl-repo
│   └── W1PD177852
│       └── v1
│           └── content
│               ├── I1KG18331
│               │   └── archive
│               ├── I1KG18332..I1KG18367
│               │   └── archive
│               └── I1KG18368
│                   └── archive
└── ocfl-work

```

### RepoNHash Layout

```shell

❯ tree -d -L 9 RepoNHash
RepoNHash
├── ocfl-repo
│   ├── 17d
│   │   └── 085
│   │       └── e85
│   │           └── 17d085e85d2a80ffb6c23de83790e4bcab9c3cb75c5f996282e8c42dd7182276
│   │               └── v1
│   │                   └── content
│   │                       ├── I1KG18331
│   │                       │   └── archive
│   │                       ├── I1KG18332
│   │                       │   └── archive
│   │                       ├── I1KG18333..I1KG13367
│   │                       │   └── archive
│   │                       ├── I1KG18368
│   │                       │   └── archive
```

## Accessing objects
The repository storage layout is hidden behind the OCFL API. Objects are accessed by their objectId, which needs to
be managed separately. That is the function of higher order systems, suche as Fedora Commons or Islandora.

Regardless of the path, (eg. `17d/085/e85/17d085385.....`) the object is W1PD177852.

Note also that the that the OS presents are *not* the repository itself. In the example above, the `images` and `sources` directories 
do not appear. They are referenced only in the `inventory.json` file  in the repository root and in each version.
Clients *always* need the API to extract content from the repository. You *cannot* simply copy, a portion of the archive
to extract it.