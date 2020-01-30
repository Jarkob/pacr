""" Contains type definitions for the needed types. """
from enum import Enum
from typing import List, Dict, Any


class ResultInterpretation(Enum):
    """ Defines how a result is interpreted. """
    LESS_IS_BETTER = "LESS_IS_BETTER"
    MORE_IS_BETTER = "MORE_IS_BETTER"
    NEUTRAL = "NEUTRAL"


class Property:
    """ A single benchmarked property. """

    def __init__(self,
                 results: List[float],
                 unit: str,
                 result_interpretation: ResultInterpretation,
                 error: str):
        self.results = results.copy() if results else []
        self.unit = unit
        self.result_interpretation = result_interpretation
        self.error = error

    def __str__(self):
        return f"{self.results}, {self.unit}," +\
            f" {self.result_interpretation}, {self.error}"

    def is_error(self):
        """ Returns whether this output encodes a global error. """
        return self.error

    def json_dict(self) -> Dict[str, Any]:
        """ Returns all properties in a dict that should be
            serialized to JSON.
        """
        return {'results': self.results,
                'unit': self.unit,
                'resultInterpretation': self.result_interpretation,
                'error': self.error}


class Benchmark:
    """ A single benchmark consisting of multiple properties. """

    def __init__(self, properties: Dict[str, Property]):
        self.properties = properties

    def __str__(self):
        formatted_properties = [str(x) for x in self.properties]
        return f"[{', '.join(formatted_properties)}]"

    def json_dict(self) -> Dict[str, Any]:
        """ Returns all properties in a dict that should be
            serialized to JSON.
        """
        return self.properties


class ScriptOutput:
    """ The wrapper object for the script output. """

    def __init__(self, benchmarks: Dict[str, Benchmark], error: str):
        self.benchmarks = benchmarks.copy() if benchmarks else {}
        self.error = error

    def __str__(self):
        formatted_benchmarks = [
            str(x) for x in self.benchmarks] if self.benchmarks else []
        return f"{', '.join(formatted_benchmarks)}, {self.error}"

    def is_error(self):
        """ Returns whether this output encodes a global error. """
        return self.error

    def json_dict(self) -> Dict[str, Any]:
        """ Returns all properties in a dict that should be
            serialized to JSON.
        """
        dictionary: Dict[str, Any] = {"error": self.error}

        for (key, value) in self.benchmarks.items():
            dictionary[key] = value

        return dictionary
