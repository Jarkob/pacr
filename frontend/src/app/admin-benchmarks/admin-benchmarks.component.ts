import { FormControl, Validators, FormGroup } from '@angular/forms';
import { BenchmarkGroup } from './../classes/benchmark-group';
import { Benchmark } from './../classes/benchmark';
import { BenchmarkService } from './../services/benchmark.service';
import { StringService } from './../services/strings.service';
import { Component, OnInit } from '@angular/core';
import { transferArrayItem, CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';

export class Group {
  id: number;
  name: string;
  benchmarks: Benchmark[];
}

@Component({
  selector: 'app-admin-benchmarks',
  templateUrl: './admin-benchmarks.component.html',
  styleUrls: ['./admin-benchmarks.component.css']
})
export class AdminBenchmarksComponent implements OnInit {

  constructor(
    private benchmarkService: BenchmarkService,
    private stringService: StringService
  ) { }

  strings: any;

  dropped = false;
  selectedBenchmark: Benchmark;

  public editBenchmarkForm: FormGroup;

  editNameControl: FormControl;
  editDescriptionControl: FormControl;
  editGroupControl: FormControl;

  benchmarkGroups: Group[] = [];

  ngOnInit() {
    this.stringService.getAdminBenchmarksStrings().subscribe(
      data => {
        this.strings = data;
      }
    );

    this.getBenchmarkGroups();

    this.initEditFormControls();
  }

  private async getBenchmarkGroups() {
    this.benchmarkGroups = [];

    const groups: BenchmarkGroup[] = await this.benchmarkService.getAllGroups().toPromise();

    groups.forEach(group => {
      const benchmarkGroup: Group = new Group();
      benchmarkGroup.id = group.id;
      benchmarkGroup.name = group.name;

      this.benchmarkService.getBenchmarksByGroup(group.id).subscribe(
        data => {
          benchmarkGroup.benchmarks = data;
        }
      );

      this.benchmarkGroups.push(benchmarkGroup);
    });
  }

  drop(event: CdkDragDrop<string[]>) {
    if (event.previousContainer === event.container) {
      moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
    } else {
      this.dropped = true;
      transferArrayItem(event.previousContainer.data,
                        event.container.data,
                        event.previousIndex,
                        event.currentIndex);
    }
  }

  public hasEditError = (controlName: string, errorName: string) => {
    return this.editBenchmarkForm.controls[controlName].hasError(errorName);
  }

  private initEditFormControls() {
    this.editNameControl = new FormControl('', [Validators.required]);
    this.editDescriptionControl = new FormControl('', [Validators.required]);
    this.editGroupControl = new FormControl(null, [Validators.required]);

    this.editBenchmarkForm = new FormGroup({
      name: this.editNameControl,
      description: this.editDescriptionControl,
      group: this.editGroupControl
    });
  }

  private loadBenchmarkData(benchmark: Benchmark) {
    this.editNameControl.setValue(benchmark.customName);
    this.editDescriptionControl.setValue(benchmark.description);
    this.editGroupControl.setValue(benchmark.benchmarkGroup.id);
  }

  public onSaveBenchmarkGroups() {
    this.dropped = false;
  }

  public onCancelBenchmarkGroups() {
    this.dropped = false;
    this.getBenchmarkGroups();
  }

  public selectBenchmark(benchmark: Benchmark) {
    this.selectedBenchmark = benchmark;
    this.loadBenchmarkData(benchmark);
  }

  public editBenchmark(benchmark: Benchmark) {

  }

  public onCancelEditBenchmark() {
    this.loadBenchmarkData(this.selectedBenchmark);
  }

}
