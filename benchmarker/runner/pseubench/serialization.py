import json
from .benchmark_types import ResultInterpretation


def serialize_type(obj):
    """ Serializes a type. """

    return json.dumps(obj, default=_type_serialization_helper)


def _type_serialization_helper(obj):
    """ A small helper to serialize unknown classes. """

    if isinstance(obj, ResultInterpretation):
        return obj.value
    try:
        result_dict = obj.json_dict().copy()
    except AttributeError:
        result_dict = obj.__dict__.copy()

    # Filter out None
    for key, value in list(result_dict.items()):
        if not value:
            del result_dict[key]

    return result_dict
