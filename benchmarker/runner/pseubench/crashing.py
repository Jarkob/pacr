""" Contains a few different mechanisms for crashing the program. """
from abc import ABC, abstractmethod
from ctypes import pointer, c_char
import sys
import random

from .benchmark_types import ScriptOutput, Property, ResultInterpretation
from .serialization import serialize_type


class CrashMechanism(ABC):
    """ The base class for crash mechanisms.
        Crash mechanisms might exit the program, they are not forced though,
        so you can chain them if appropriate.
    """

    @abstractmethod
    def crash(self):
        """ Crashes the program in some way. """


class Segfaulter(CrashMechanism):
    """ Causes a segfault that kills the program. """

    def crash(self):
        null_pointer = pointer(c_char.from_address(7))
        null_pointer[0] = 20


class InvalidJsonWriter(CrashMechanism):
    """ Writes invalid json output and does not exit. """

    def crash(self):
        print(r'{ "TestBenchmark": }')


class IncompleteJsonWriter(CrashMechanism):
    """ Writes incomplete json output (missing some fields)
        and does not exit.
    """

    def crash(self):
        # Misses result interpretation
        benchmark_property = Property(
            [], "cats", None, "Hey"
        )
        benchmark_property.results = None
        benchmarks = [benchmark_property]
        output = ScriptOutput(benchmarks, None)
        print(serialize_type(output))


class NoBenchmarkAndNoErrorWriter(CrashMechanism):
    """ Writes '{}' and does not exit. """

    def crash(self):
        print("{}")


class NoResultsAndNoErrorWriter(CrashMechanism):
    """ Writes a ScriptOutput that has a single property which has neither
        a result nor an error and does not exit.
    """

    def crash(self):
        benchmark_property = Property(
            [], "cats", ResultInterpretation.LESS_IS_BETTER, None
        )
        benchmark_property.results = None
        benchmarks = [benchmark_property]
        output = ScriptOutput(benchmarks, None)
        print(serialize_type(output))


class RandomNonZeroExitCode(CrashMechanism):
    """ Exits with a random non-zero exit code. """

    def crash(self):
        sys.exit(random.randrange(1, 255))
