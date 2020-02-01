import { ResultInterpretation } from './../classes/result-interpretation';
import { BenchmarkGroup } from './../classes/benchmark-group';
import { BenchmarkingResult } from './../classes/benchmarking-result';
import { BenchmarkService } from './../services/benchmark.service';
import { CommitBenchmarkingResult } from './../classes/commit-benchmarking-result';
import { BenchmarkingResultService } from './../services/benchmarking-result.service';
import { StringService } from './../services/strings.service';
import { COMMIT_HASH_DATA } from './../detail-view/detail-view-maximized.tokens';
import { DetailViewMaximizedRef } from './../detail-view/detail-view-maximized-ref';
import { Component, Inject, HostListener, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material';

const ESCAPE_KEY = 27;

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
  median: number;
  lowerQuartile: number;
  upperQuartile: number;
  standardDeviation: number;
  mean: number;
  hadLocalError: boolean;
  errorMessage: string;
  ratioToPreviousCommit: number;
  compared: boolean;
}

@Component({
  selector: 'app-detail-view-maximized',
  templateUrl: './detail-view-maximized.component.html',
  styleUrls: ['./detail-view-maximized.component.css']
})
export class DetailViewMaximizedComponent implements OnInit {

  strings: any;
  benchmarkingResult: CommitBenchmarkingResult;

  displayedColumns: string[] = ['property', 'median', 'lowerQuartile', 'upperQuartile', 'standardDeviation', 'mean'];

  groupByColumns: string[] = ['benchmarkGroup', 'benchmark'];

  public dataSource = new MatTableDataSource<Result | Group>([]);

  constructor(
    @Inject(COMMIT_HASH_DATA) public commitHash: string,
    public dialogRef: DetailViewMaximizedRef,
    private stringService: StringService,
    private benchmarkingResultService: BenchmarkingResultService,
    private benchmarkService: BenchmarkService
  ) {}

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

  hadLocalError(index, item): boolean {
    return item.hadLocalError;
  }

  @HostListener('document:keydown', ['$event']) handleKeydown(event: KeyboardEvent) {
    if (event.keyCode === ESCAPE_KEY) {
      this.dialogRef.close();
    }
  }

  ngOnInit() {
    this.stringService.getDetailViewStrings().subscribe(
      data => {
        this.strings = data;
      }
    );

    this.dataSource.filterPredicate = this.customFilterPredicate.bind(this);
    this.selectCommit(this.commitHash);
    this.dataSource.filter = performance.now().toString(); // trigger filter update
  }

  private selectCommit(sha: string): void {
    if (sha === null || sha === '') {
      return;
    }

    this.benchmarkingResultService.getBenchmarkingResultsForCommit(sha).subscribe(
      data => {
        this.benchmarkingResult = data;

        const resultArray = this.buildResultArray(this.benchmarkingResult);

        resultArray.then(value => {
          this.dataSource.data = this.addGroups(value, this.groupByColumns);
        });
      }
    );
  }

  async buildResultArray(benchmarkingResult: CommitBenchmarkingResult): Promise<Result[]> {
    const resultData: Result[] = [];
    let benchmarkGroups: BenchmarkGroup[];

    benchmarkGroups = await this.benchmarkService.getAllGroups().toPromise();

    benchmarkingResult.benchmarksList.forEach(benchmark => {
      const groupName = this.findBenchmarkGroupName(benchmark.groupId, benchmarkGroups);

      benchmark.results.forEach(benchProperty => {
        resultData.push({benchmarkGroup: groupName, benchmark: benchmark.customName, property: benchProperty.name,
          median: benchProperty.median, mean: benchProperty.mean, lowerQuartile: benchProperty.lowerQuartile,
          upperQuartile: benchProperty.upperQuartile, standardDeviation: benchProperty.standardDeviation,
          unit: benchProperty.unit, interpretation: benchProperty.interpreation, hadLocalError: benchProperty.hadLocalError,
          errorMessage: benchProperty.errorMessage, ratioToPreviousCommit: benchProperty.ratioToPreviousCommit,
          compared: benchProperty.compared  });
      });
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

  close() {
    this.dialogRef.close();
  }
}
