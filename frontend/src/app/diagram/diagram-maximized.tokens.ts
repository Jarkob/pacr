import { Repository } from './../classes/repository';
import { BenchmarkGroup } from './../classes/benchmark-group';
import { BenchmarkProperty } from './../classes/benchmark-property';
import { Benchmark } from './../classes/benchmark';
import { InjectionToken } from '@angular/core';
import { Dataset } from '../classes/dataset';

export const SELECTED_BENCHMARK = new InjectionToken<Benchmark>('SELECTED_BENCHMARK');
export const SELECTED_PROPERTY = new InjectionToken<BenchmarkProperty>('SELECTED_PROPERTY');
export const SELECTED_DATASETS = new InjectionToken<Dataset[]>('SELECTED_DATASETS');
export const REPOSITORIES = new InjectionToken<Map<number, Repository>>('REPOSITORIES');
export const GROUPS = new InjectionToken<BenchmarkGroup[]>('GROUPS');
export const BENCHMARKS = new InjectionToken<Map<string, Benchmark[]>>('BENCHMARKS');
export const RESULTS = new InjectionToken<Map<number, Map<string, Map<string, any>>>>('RESULTS');
