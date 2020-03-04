import { BenchmarkProperty } from './../classes/benchmark-property';
import { Benchmark } from './../classes/benchmark';
import { InjectionToken } from '@angular/core';
import { Dataset } from '../classes/dataset';

export const SELECTED_BENCHMARK = new InjectionToken<Benchmark>('SELECTED_BENCHMARK');
export const SELECTED_PROPERTY = new InjectionToken<BenchmarkProperty>('SELECTED_PROPERTY');
export const SELECTED_DATASETS = new InjectionToken<Dataset[]>('SELECTED_DATASETS');
