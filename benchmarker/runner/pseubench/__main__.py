""" The main entry file """

import random
import sys
from pathlib import Path
from argparse import ArgumentParser

from .generation import RandomScriptOutput
from .serialization import serialize_type
from .crashing import Segfaulter, InvalidJsonWriter, IncompleteJsonWriter, NoBenchmarkAndNoErrorWriter, NoResultsAndNoErrorWriter, RandomNonZeroExitCode
from .real_data import fetch_correct_data


def _syserr(message: str):
    """ Writes to STDERR. """
    print(message, file=sys.stderr)


def _build_parser() -> ArgumentParser:
    parser: ArgumentParser = ArgumentParser(
        prog="Pseubench",
        description="Generates random data, mimicking the benchmark script."
        + "Can also crash in nice ways."
    )
    parser.add_argument("benchmark_repo", type=str,
                        help="the path to the benchmark repo")
    parser.add_argument("--crash",
                        action="store_const",
                        const=True,
                        help="whether to crash somehow")
    parser.add_argument("--max-sizes",
                        type=int,
                        help="Max amount of properties, benchmarks" +
                        " and results (default: 2)",
                        default=2)
    parser.add_argument("--real-data",
                        type=str,
                        help="Path to a real json data file",
                        required=False)
    return parser


def _build_output(max_length: int):
    """ Builds a random script output. """
    return RandomScriptOutput(max_length).create()


def _crash():
    crash_elements = [
        Segfaulter(),
        InvalidJsonWriter(),
        IncompleteJsonWriter(),
        NoBenchmarkAndNoErrorWriter(),
        NoResultsAndNoErrorWriter(),
        RandomNonZeroExitCode()
    ]
    picked = random.choice(crash_elements)
    picked.crash()


def main():
    """ The application main method. """
    result = _build_parser().parse_args()
    if result.crash:
        _crash()
        return

    if result.real_data:
        data = fetch_correct_data(Path(result.real_data),
                                  Path(result.benchmark_repo))
        print(str(data))
        return

    script_output = _build_output(result.max_sizes)
    print(serialize_type(script_output))


if __name__ == '__main__':
    main()
