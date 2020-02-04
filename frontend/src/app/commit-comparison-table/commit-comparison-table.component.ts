import { BenchmarkService } from './../services/benchmark.service';
import { BenchmarkGroup } from './../classes/benchmark-group';
import { StringService } from './../services/strings.service';
import { BenchmarkingResultService } from './../services/benchmarking-result.service';
import { OutputBenchmark } from './../classes/output-benchmark';
import { MatTableDataSource } from '@angular/material';
import { CommitBenchmarkingResult } from './../classes/commit-benchmarking-result';
import { Component, OnInit, Input } from '@angular/core';

export class Group {
  level = 0;
  parent: Group;
  expanded = false;

  constructor(
    level: number,
    parent: Group,
    expanded: boolean
  ) {
    this.level = level;
    this.parent = parent;
    this.expanded = expanded;
  }

  get visible(): boolean {
    return !this.parent || (this.parent.visible && this.parent.expanded);
  }
}

export interface Result {
  benchmarkGroup: string;
  benchmark: string;
  property: string;
  unit: string;
  interpretation: string;
  result1: number;
  result2: number;
  ratio1: number;
  ratio2: number;
}

@Component({
  selector: 'app-commit-comparison-table',
  templateUrl: './commit-comparison-table.component.html',
  styleUrls: ['./commit-comparison-table.component.css']
})
export class CommitComparisonTableComponent implements OnInit {

  constructor(
    private resultService: BenchmarkingResultService,
    private stringService: StringService,
    private benchmarkService: BenchmarkService
  ) { }

  @Input() commitHash1: string;
  @Input() commitHash2: string;

  strings: any;

  benchmarkingResult1: CommitBenchmarkingResult;
  benchmarkingResult2: CommitBenchmarkingResult;

  // contains a list of [benchmark1, benchmark2] where benchmark1 and benchmark2 have the same id
  comparableBenchmarks: OutputBenchmark[][] = [];

  displayedColumns: string[] = ['property', 'result1', 'result2', 'interpretation'];

  groupByColumns: string[] = ['benchmarkGroup', 'benchmark'];

  public dataSource = new MatTableDataSource<Result | Group>([]);

  ngOnInit() {
    this.stringService.getCommitComparisonTableStrings().subscribe(
      data => {
        this.strings = data;
      }
    );

    this.dataSource.filterPredicate = this.customFilterPredicate.bind(this);
    this.getBenchmarkingResults();
    this.dataSource.filter = performance.now().toString(); // trigger filter update
  }

  compareBenchmarkingResults() {
    const benchmarks = new Map();

    this.benchmarkingResult1.benchmarksList.forEach(bench => {
      benchmarks.set(bench.id, bench);
    });

    this.benchmarkingResult2.benchmarksList.forEach(bench => {
      if (benchmarks.has(bench.id)) {
        this.comparableBenchmarks.push([benchmarks.get(bench.id), bench]);
      }
    });

    const resultArray = this.buildResultArray();

    resultArray.then(value => {
      this.dataSource.data = this.addGroups(value, this.groupByColumns);
    });
  }

  async getBenchmarkingResults() {
    this.benchmarkingResult1 = await this.resultService.getBenchmarkingResultsForCommit(this.commitHash1).toPromise();

    this.benchmarkingResult2 = await this.resultService.getBenchmarkingResultsForCommit(this.commitHash2).toPromise();

    this.compareBenchmarkingResults();
  }

  customFilterPredicate(data: Result | Group, filter: string): boolean {
    return (data instanceof Group) ? data.visible : this.getDataRowVisible(data);
  }

  getDataRowVisible(data: Result): boolean {
    const groupRows = this.dataSource.data.filter(
      row => {
        if (!(row instanceof Group)) {
          return false;
        }

        let match = true;
        this.groupByColumns.forEach(
          column => {
            if (!row[column] || !data[column] || row[column] !== data[column]) {
              match = false;
            }
          }
        );

        return match;
      }
    );

    if (groupRows.length === 0) {
      return true;
    }

    if (groupRows.length > 1) {
      throw new Error('Data row is in more than one group!');
    }

    const parent = groupRows[0] as Group;

    return parent.visible && parent.expanded;
  }

  groupHeaderClick(row) {
    row.expanded = !row.expanded;
    this.dataSource.filter = performance.now().toString(); // trigger filter update
  }

  addGroups(data: any[], groupByColumns: string[]): any[] {
    const rootGroup = new Group(-1, null, true);

    return this.getSublevel(data, 0, groupByColumns, rootGroup);
  }

  getSublevel(data: any[], level: number, groupByColumns: string[], parent: Group): any[] {
    // Recursive function, stop when there are no more levels.
    if (level >= groupByColumns.length) {
      return data;
    }

    const groups = this.uniqueBy(
      data.map(
        row => {
          const result = new Group(level + 1, parent, level === 1);

          for (let i = 0; i <= level; i++) {
            result[groupByColumns[i]] = row[groupByColumns[i]];
          }

          return result;
        }
      ),
      JSON.stringify);

    const currentColumn = groupByColumns[level];

    let subGroups = [];
    groups.forEach(group => {
      const rowsInGroup = data.filter(row => group[currentColumn] === row[currentColumn]);
      const subGroup = this.getSublevel(rowsInGroup, level + 1, groupByColumns, group);
      subGroup.unshift(group);
      subGroups = subGroups.concat(subGroup);
    });

    return subGroups;
  }

  uniqueBy(a, key) {
    const seen = {};
    return a.filter((item) => {
      const k = key(item);
      return seen.hasOwnProperty(k) ? false : (seen[k] = true);
    });
  }

  isGroup(index, item): boolean {
    return item.level;
  }

  async buildResultArray(): Promise<Result[]> {
    const resultData: Result[] = [];
    let benchmarkGroups: BenchmarkGroup[];

    benchmarkGroups = await this.benchmarkService.getAllGroups().toPromise();

    this.comparableBenchmarks.forEach(benchmarkPair => {
      const bench = benchmarkPair[0];

      const groupName = this.findBenchmarkGroupName(bench.groupId, benchmarkGroups);

      for (let i = 0; i < bench.results.length; i++) {
        const benchProperty = bench.results[i];

        const median1 = benchmarkPair[0].results[i].median;
        const median2 = benchmarkPair[1].results[i].median;

        const medianRatio1 = median1 / median2;
        const medianRatio2 = median2 / median1;

        resultData.push({benchmarkGroup: groupName, benchmark: bench.customName, property: benchProperty.name,
          unit: benchProperty.unit, result1: median1, result2: median2,
          interpretation: benchProperty.interpretation, ratio1: medianRatio1, ratio2: medianRatio2});
      }
    });

    return resultData;
  }

  private findBenchmarkGroupName(id: number, benchmarkGroups: BenchmarkGroup[]): string {
    if (id < 0) {
      // return this.strings.undefinedGroup;
      return 'Undefined Group';
    }

    benchmarkGroups.forEach(group => {
      if (group.id === id) {
        return group.name;
      }
    });

    // return this.strings.undefinedGroup;
    return 'Undefined Group';
  }

}
