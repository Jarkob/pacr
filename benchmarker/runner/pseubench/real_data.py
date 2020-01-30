""" Allows replaying real data. """
from typing import Any, Dict
from subprocess import check_output
from pathlib import Path
import json


def _fetch_git_commit(directory: Path) -> str:
    """ Raises CalledProcessError if the lookup fails!

        returns: the git commit hash
    """
    escaped_file_name = str(directory).replace("'", "\\'")
    args = ["/bin/sh", "-c", f"cd '{escaped_file_name}' && git rev-parse HEAD"]
    res = check_output(args, universal_newlines=True, encoding='UTF-8')
    return res.strip()


def _fetch_matching(directory: Path, json_obj: Dict[str, Any]) -> Any:
    """ Extracts the entry from the passed dict that matches the
        current git commit.

        returns: the resulting object
    """
    current_commit = _fetch_git_commit(directory)

    return json_obj["commits"][current_commit]


def _load_file(file: Path) -> Dict[str, Any]:
    """ Loads a file as a json dict.
        Resulting dict has the form:

        commits: {...} Map from commit hash to output
        name: {...}    Name of project
        url: {...}     Download URL
    """
    loaded = json.load(open(file))
    return loaded[0]


def fetch_correct_data(data_file: Path, work_repo: Path) -> str:
    """ Fetches the correct data for the work_repo state from the
        data_file and returns it as a string.

        returns: the data to write out as a string
    """
    file_content: Dict[str, Any] = _load_file(data_file)
    data = _fetch_matching(work_repo, file_content)
    return json.dumps(data)
