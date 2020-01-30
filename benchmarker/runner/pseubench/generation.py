""" Generates random objects. """
import pathlib
import random
from abc import ABC, abstractmethod
from typing import Generic, List, TypeVar

from .benchmark_types import (Benchmark, Property, ResultInterpretation,
                              ScriptOutput)

T = TypeVar("T")


class TypeGenerator(Generic[T], ABC):
    """ A generator for a single type. """

    @abstractmethod
    def create(self) -> T:
        """ Creates an instance of a given type. """
        pass


class ResultInterpretationGenerator(TypeGenerator[ResultInterpretation]):
    """ Creates random ResultInterpretations. """

    def create(self):
        return random.choice(list(ResultInterpretation))


class RandomStringGenerator(TypeGenerator[str]):
    """ Creates random strings. """

    dictionary: List[str]
    dict_path = pathlib.Path(__file__).parent / "resources" / "british.dict"
    with open(dict_path) as f:
        dictionary = [line.strip() for line in f]

    def __init__(self, component_length=3):
        self.component_length = component_length

    def create(self):
        result: List[str] = []
        for _ in range(self.component_length):
            result.append(random.choice(RandomStringGenerator.dictionary))
        return " ".join(result)


class RandomPropertyGenerator(TypeGenerator[Property]):
    """ Creates a random Property. """

    def __init__(self, max_results: int):
        self.max_results = max_results

    def create(self):
        unit: str = _random_string(length=1)
        result_interpretation = _random_interpretation()

        if random.random() < 0.5:
            error = _random_string(20)
            return Property(None, None, None, error)

        results = []
        for _ in range(random.randint(1, self.max_results)):
            results.append(random.random() * random.randint(0, 2**32))

        return Property(results, unit, result_interpretation, None)


class RandomBenchmarkGenerator(TypeGenerator[Benchmark]):
    """ Creates a random Benchmark. """

    def __init__(self, max_properties: int):
        self.max_properties = max_properties

    def create(self):
        properties = dict()
        for _ in range(random.randint(1, self.max_properties)):
            properties[_random_string(length=1)] = _random_property(
                self.max_properties)
        return Benchmark(properties)


class RandomScriptOutput(TypeGenerator[ScriptOutput]):
    """ Creates a random ScriptOutput. """

    def __init__(self, max_benchmarks: int):
        self.max_benchmarks = max_benchmarks

    def create(self):

        if random.random() < 0.5:
            error = _random_string(20)
            return ScriptOutput(None, error)

        benchmarks = dict()
        for _ in range(random.randint(1, self.max_benchmarks)):
            benchmarks[_random_string(length=1)] = _random_benchmark(
                self.max_benchmarks)

        return ScriptOutput(benchmarks, None)


def _random_string(length: int = 3):
    return RandomStringGenerator(length).create()


def _random_interpretation():
    return ResultInterpretationGenerator().create()


def _random_property(max_results: int = 10):
    return RandomPropertyGenerator(max_results).create()


def _random_benchmark(max_properties: int = 2):
    return RandomBenchmarkGenerator(max_properties).create()
