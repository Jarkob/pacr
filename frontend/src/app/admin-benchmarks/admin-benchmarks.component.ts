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
  expanded: boolean;

  constructor(
    id: number,
    name: string
  ) {
    this.id = id;
    this.name = name;
    this.expanded = false;
   }
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

  public addBenchmarkGroupForm: FormGroup;
  addGroupNameControl: FormControl;

  public editBenchmarkGroupForm: FormGroup;
  editGroupNameControl: FormControl;

  selectedBenchmarkGroup: Group;

  public editBenchmarkForm: FormGroup;
  editNameControl: FormControl;
  editDescriptionControl: FormControl;
  editGroupControl: FormControl;

  selectedBenchmark: Benchmark;

  benchmarkGroups: Group[] = [];

  ngOnInit() {
    this.stringService.getAdminBenchmarksStrings().subscribe(
      data => {
        this.strings = data;
      }
    );

    this.getBenchmarkGroups();

    this.initEditFormControls();
    this.initAddGroupFormControls();
    this.initEditGroupFormControls();
  }

  async getBenchmarkGroups() {
    this.benchmarkGroups = [];

    const groups: BenchmarkGroup[] = await this.benchmarkService.getAllGroups().toPromise();

    groups.forEach(group => {
      const benchmarkGroup: Group = new Group(group.id, group.name);

      this.benchmarkService.getBenchmarksByGroup(group.id).subscribe(
        data => {
          benchmarkGroup.benchmarks = data;
        }
      );

      this.benchmarkGroups.push(benchmarkGroup);
    });

    // fetch all benchmarks which don't belong to a group.
    const undefinedGroupBenchmarks = await this.benchmarkService.getBenchmarksByGroup(-1).toPromise();

    // add a special group if benchmarks without a group exist.
    if (undefinedGroupBenchmarks && undefinedGroupBenchmarks.length > 0) {
      const undefinedGroup: Group = new Group(-1, 'Undefined Group');
      undefinedGroup.benchmarks = undefinedGroupBenchmarks;

      this.benchmarkGroups.push(undefinedGroup);
    }
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

  hasEditError = (controlName: string, errorName: string) => {
    return this.editBenchmarkForm.controls[controlName].hasError(errorName);
  }

  hasAddGroupError = (controlName: string, errorName: string) => {
    return this.addBenchmarkGroupForm.controls[controlName].hasError(errorName);
  }

  hasEditGroupError = (controlName: string, errorName: string) => {
    return this.editBenchmarkGroupForm.controls[controlName].hasError(errorName);
  }

  initEditFormControls() {
    this.editNameControl = new FormControl('', [Validators.required]);
    this.editDescriptionControl = new FormControl('', [Validators.required]);
    this.editGroupControl = new FormControl(null, [Validators.required]);

    this.editBenchmarkForm = new FormGroup({
      name: this.editNameControl,
      description: this.editDescriptionControl,
      group: this.editGroupControl
    });
  }

  toggleGroup(group: Group) {
    group.expanded = !group.expanded;
  }

  selectBenchmark(benchmark: Benchmark) {
    this.selectedBenchmark = benchmark;
    this.loadBenchmarkData(benchmark);
  }

  loadBenchmarkData(benchmark: Benchmark) {
    this.editNameControl.setValue(benchmark.customName);
    this.editDescriptionControl.setValue(benchmark.description);
    this.editGroupControl.setValue(benchmark.benchmarkGroup.id);
  }

  onCancelEditBenchmark() {
    this.loadBenchmarkData(this.selectedBenchmark);
  }

  selectBenchmarkGroup(group: Group) {
    this.selectedBenchmarkGroup = group;
    this.loadBenchmarkGroupData(this.selectedBenchmarkGroup);
  }

  initEditGroupFormControls() {
    this.editGroupNameControl = new FormControl('', [Validators.required]);

    this.editBenchmarkGroupForm = new FormGroup({
      name: this.editGroupNameControl
    });
  }

  loadBenchmarkGroupData(benchmarkGroup: Group) {
    this.editGroupNameControl.setValue(benchmarkGroup.name);
  }

  editBenchmark(benchmark: Benchmark) {
    this.benchmarkService.updateBenchmark({
      id: benchmark.id,
      customName: benchmark.customName,
      description: benchmark.description,
      groupId: benchmark.benchmarkGroup.id
    }).subscribe();
  }

  addBenchmarkGroup(form: any) {
    console.log(form);
    this.benchmarkService.addGroup(form.name).subscribe();
  }

  editBenchmarkGroup() {
    this.benchmarkService.updateGroup({
      id: this.selectedBenchmarkGroup.id,
      name: this.selectedBenchmarkGroup.name
    }).subscribe();
  }

  deleteBenchmarkGroup() {
    this.benchmarkService.deleteGroup(this.selectedBenchmarkGroup.id);
  }

  onCancelEditBenchmarkGroup() {
    this.loadBenchmarkGroupData(this.selectedBenchmarkGroup);
  }

  onSaveBenchmarkGroups() {
    this.dropped = false;

    for (const group of this.benchmarkGroups) {
      for (const benchmark of group.benchmarks) {
        this.benchmarkService.updateBenchmark({
          id: benchmark.id,
          customName: benchmark.customName,
          description: benchmark.description,
          groupId: group.id
        }).subscribe();
      }
    }
  }

  onCancelBenchmarkGroups() {
    this.dropped = false;
    this.getBenchmarkGroups();
  }

  initAddGroupFormControls() {
    this.addGroupNameControl = new FormControl('', [Validators.required]);

    this.addBenchmarkGroupForm = new FormGroup({
      name: this.addGroupNameControl
    });
  }

  onCancelAddBenchmarkGroup() {
    this.initAddGroupFormControls();
  }
}
