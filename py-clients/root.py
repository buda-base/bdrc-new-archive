#!/usr/bin/env python3
"""
test of ofcl-py client
"""
import argparse
import os

import ocfl

from pathlib import Path


dispositions:[str] = [ 'pairtree' 'tripletree','quadtree', 'uuid_quadtree', 'identity']
arg_id:str = 'W1PD1577582'

# Create a class from an argparse.Namespace
class VersionMetadataArgs(argparse.Namespace):
    created: str =
    message: str
    name: str
    address: str


if __name__ == '__main__':
    repo_root: Path = Path.home / 'dev' / 'tmp' / 'Projects' / "OCFL"  /  "ofcl-py"

    os.makedirs(repo_root, exist_ok=True)


    # Make several repos, add the W1PD1577582 to it, see what we get
    # See buda-base/bdrc-new-archive/OcflClient's io.ocfl.core.extension.storage.layout for the
    # actual layout names.
    #
    # For take 1, we'll only use the "dispostions" that ocfl-py supports natively:



    for dispo in dispositions:
        repo_archive: Path = repo_root / dispo / 'ocfl-data'
        repo_work: Path = repo_root /  dispo / 'ocfl-work'

        repo = ocfl.Store(root=repo_archive, dispo=dispo)

        # Cribbed from ocfl-py/ocfl-object.py
        # First, you need a VersionMetadata:
        vm: VersionMetadataArgs = VersionMetadataArgs()
        created: str
message: str
name: str
address: str

        if args is not None:
    self.created = args.created
    self.message = args.message
    self.name = args.name
    self.address = args.address
        obj = ocfl.Object(identifier=arg_id, create=True)
if args.create:
    srcdir = args.srcdir
    metadata = ocfl.VersionMetadata(args=args)
    if args.srcbag is not None:
        srcdir = ocfl.bag_as_source(args.srcbag, metadata)
        if metadata.id is not None:
            if obj.id:
                if obj.id != metadata.id:
                    raise FatalError("Identifier specified (%s) and identifier from Bagit bag (%s) do not match!" % (obj.id, metadata.id))
            else:
                obj.id = metadata.id
    elif args.srcdir is None:
        raise FatalError("Must specify either --srcdir or --srcbag containing v1 files when creating an OCFL object!")
    obj.create(srcdir=srcdir,
               metadata=metadata,
               objdir=args.objdir)
        ocfl_object = ocfl.Object(root=repo_work, id='W1PD1577582', path=).load()