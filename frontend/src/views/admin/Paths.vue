<template>
  <div class="admin-page">
    <div class="admin-page-header">
      <h3 class="admin-page-title">路径管理</h3>
      <p class="admin-page-subtitle">创建、编辑和管理学习路径</p>
    </div>
    <div class="admin-actions">
      <el-button type="primary" size="medium" @click="showCreateDialog = true">
        <i class="el-icon-plus"></i> 新建路径
      </el-button>
      <el-button type="success" size="medium" @click="showGenerateDialog = true" :loading="generating"
        icon="el-icon-magic-stick"> AI 生成
      </el-button>
    </div>
    <div class="admin-table-wrap">
      <el-table :data="paths" v-loading="loading" stripe size="medium">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" min-width="160" />
        <el-table-column prop="difficulty" label="难度" width="90">
          <template slot-scope="{row}">
            <el-tag size="mini" effect="plain"
              :type="row.difficulty === 'BEGINNER' ? 'success' : row.difficulty === 'INTERMEDIATE' ? 'warning' : 'danger'">
              {{ row.difficulty }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="courseCount" label="课程数" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="90">
          <template slot-scope="{row}">
            <el-tag :type="row.status==='PUBLISHED'?'success':'info'" size="mini" effect="plain">
              {{ row.status === 'PUBLISHED' ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" align="center">
          <template slot-scope="{row}">
            <el-button size="mini" type="primary" plain @click="handleEdit(row)">编辑</el-button>
            <el-button size="mini" type="success" plain @click="handleManageCourses(row)">课程</el-button>
            <el-button size="mini" type="danger" plain @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="total > pageSize" class="table-pagination">
        <el-pagination
          background layout="prev, pager, next"
          :total="total" :page-size="pageSize"
          :current-page.sync="currentPage"
          @current-change="handlePageChange">
        </el-pagination>
      </div>
    </div>

    <!-- 新建路径对话框 -->
    <el-dialog title="新建路径" :visible.sync="showCreateDialog" width="520px" class="admin-dialog"
      :close-on-click-modal="false" append-to-body>
      <el-form :model="form" label-width="70px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="如：Java 从入门到精通" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input type="textarea" v-model="form.description" :rows="3"
            placeholder="简要描述这条学习路径的内容和目标" />
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="form.difficulty" style="width: 100%">
            <el-option label="入门" value="BEGINNER" />
            <el-option label="进阶" value="INTERMEDIATE" />
            <el-option label="高级" value="ADVANCED" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="草稿" value="DRAFT" />
            <el-option label="发布" value="PUBLISHED" />
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="showCreateDialog=false">取消</el-button>
        <el-button type="primary" @click="handleCreate" :loading="creating">创建</el-button>
      </span>
    </el-dialog>

    <!-- 编辑路径对话框 -->
    <el-dialog title="编辑路径" :visible.sync="showEditDialog" width="520px" class="admin-dialog"
      :close-on-click-modal="false" append-to-body>
      <el-form :model="editForm" label-width="70px">
        <el-form-item label="标题">
          <el-input v-model="editForm.title" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input type="textarea" v-model="editForm.description" :rows="3" />
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="editForm.difficulty" style="width: 100%">
            <el-option label="入门" value="BEGINNER" />
            <el-option label="进阶" value="INTERMEDIATE" />
            <el-option label="高级" value="ADVANCED" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status" style="width: 100%">
            <el-option label="草稿" value="DRAFT" />
            <el-option label="发布" value="PUBLISHED" />
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="showEditDialog=false">取消</el-button>
        <el-button type="primary" @click="handleUpdate" :loading="updating">保存</el-button>
      </span>
    </el-dialog>

    <!-- AI 生成对话框 -->
    <el-dialog title="AI 生成学习路径" :visible.sync="showGenerateDialog" width="520px" class="admin-dialog"
      :close-on-click-modal="false" append-to-body>
      <el-form label-width="80px">
        <el-form-item label="需求描述">
          <el-input type="textarea" v-model="generateRequirement" :rows="5"
            placeholder="如：Java 微服务从入门到实战，包含 Spring Cloud、Docker、Kubernetes" />
        </el-form-item>
        <p class="generate-hint">
          <i class="el-icon-info"></i>
          AI 会根据描述自动生成完整的学习路径及课程列表，保存为草稿状态
        </p>
      </el-form>
      <span slot="footer">
        <el-button @click="showGenerateDialog=false">取消</el-button>
        <el-button type="success" @click="handleGenerate" :loading="generating">
          {{ generating ? 'AI 思考中...' : '开始生成' }}
        </el-button>
      </span>
    </el-dialog>

    <!-- ===== 课程管理对话框 ===== -->
    <el-dialog title="课程管理" :visible.sync="showCourseDialog" width="800px" class="admin-dialog"
      :close-on-click-modal="false" append-to-body top="5vh">
      <div slot="title">
        <span>课程管理 — {{ currentPathTitle }}</span>
      </div>

      <div class="admin-actions" style="margin-bottom:16px">
        <el-button type="primary" size="small" @click="showCourseCreateDialog = true">
          <i class="el-icon-plus"></i> 新增课程
        </el-button>
      </div>

      <el-table :data="courses" v-loading="coursesLoading" stripe size="small" empty-text="暂无课程，点击上方按钮添加">
        <el-table-column prop="orderIndex" label="序号" width="60" align="center" />
        <el-table-column prop="title" label="标题" min-width="180" />
        <el-table-column label="类型" width="90">
          <template slot-scope="{row}">
            <el-tag size="mini" effect="plain"
              :type="row.contentType === 'VIDEO' ? 'warning' : row.contentType === 'CODING' ? 'danger' : row.contentType === 'QUIZ' ? 'success' : ''">
              {{ contentTypeLabel(row.contentType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="estimatedMinutes" label="时长(分)" width="90" align="center" />
        <el-table-column label="操作" width="180" align="center">
          <template slot-scope="{row}">
            <el-button size="mini" type="primary" plain @click="editCourse(row)">编辑</el-button>
            <el-button size="mini" type="danger" plain @click="deleteCourse(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 新增课程对话框 -->
    <el-dialog title="新增课程" :visible.sync="showCourseCreateDialog" width="750px" class="admin-dialog"
      :close-on-click-modal="false" append-to-body>
      <el-form :model="courseForm" label-width="100px" size="small">
        <el-form-item label="课程标题">
          <el-input v-model="courseForm.title" placeholder="如：Java 基础语法入门" />
        </el-form-item>
        <el-form-item label="课程描述">
          <el-input type="textarea" v-model="courseForm.description" :rows="2" placeholder="简述课程内容" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="内容类型">
              <el-select v-model="courseForm.contentType" style="width:100%">
                <el-option label="文章" value="ARTICLE" />
                <el-option label="视频" value="VIDEO" />
                <el-option label="编程" value="CODING" />
                <el-option label="测验" value="QUIZ" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="预计分钟">
              <el-input-number v-model="courseForm.estimatedMinutes" :min="5" :max="480" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="排序序号">
              <el-input-number v-model="courseForm.orderIndex" :min="1" :max="99" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="正文内容">
          <div class="markdown-editor-tabs">
            <el-radio-group v-model="courseForm._previewMode" size="mini">
              <el-radio-button label="edit"><i class="el-icon-edit"></i> 编辑</el-radio-button>
              <el-radio-button label="preview"><i class="el-icon-view"></i> 预览</el-radio-button>
            </el-radio-group>
            <span class="editor-hint" v-if="courseForm._previewMode === 'edit'">支持 Markdown 语法</span>
          </div>
          <el-input v-if="courseForm._previewMode !== 'preview'" type="textarea" v-model="courseForm.contentMarkdown" :rows="14"
            placeholder="Markdown 格式的课程正文内容&#10;支持 ## 标题、代码块、列表等" />
          <div v-else class="markdown-preview">
            <markdown-renderer :content="courseForm.contentMarkdown || '*暂无内容*'" />
          </div>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="showCourseCreateDialog=false">取消</el-button>
        <el-button type="primary" @click="handleCreateCourse" :loading="courseSaving">创建</el-button>
      </span>
    </el-dialog>

    <!-- 编辑课程对话框 -->
    <el-dialog title="编辑课程" :visible.sync="showCourseEditDialog" width="750px" class="admin-dialog"
      :close-on-click-modal="false" append-to-body>
      <el-form :model="courseEditForm" label-width="100px" size="small">
        <el-form-item label="课程标题">
          <el-input v-model="courseEditForm.title" />
        </el-form-item>
        <el-form-item label="课程描述">
          <el-input type="textarea" v-model="courseEditForm.description" :rows="2" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="内容类型">
              <el-select v-model="courseEditForm.contentType" style="width:100%">
                <el-option label="文章" value="ARTICLE" />
                <el-option label="视频" value="VIDEO" />
                <el-option label="编程" value="CODING" />
                <el-option label="测验" value="QUIZ" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="预计分钟">
              <el-input-number v-model="courseEditForm.estimatedMinutes" :min="5" :max="480" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="排序序号">
              <el-input-number v-model="courseEditForm.orderIndex" :min="1" :max="99" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="正文内容">
          <div class="markdown-editor-tabs">
            <el-radio-group v-model="courseEditForm._previewMode" size="mini">
              <el-radio-button label="edit"><i class="el-icon-edit"></i> 编辑</el-radio-button>
              <el-radio-button label="preview"><i class="el-icon-view"></i> 预览</el-radio-button>
            </el-radio-group>
            <span class="editor-hint" v-if="courseEditForm._previewMode === 'edit'">支持 Markdown 语法</span>
          </div>
          <el-input v-if="courseEditForm._previewMode !== 'preview'" type="textarea" v-model="courseEditForm.contentMarkdown" :rows="14"
            placeholder="Markdown 格式的课程正文内容" />
          <div v-else class="markdown-preview">
            <markdown-renderer :content="courseEditForm.contentMarkdown || '*暂无内容*'" />
          </div>
        </el-form-item>

        <!-- ===== 参考资料 ===== -->
        <el-divider content-position="left">参考资料</el-divider>

        <el-form-item label=" " label-width="100px">
          <div class="resource-list">
            <!-- 已有资源 -->
            <div v-for="(r, i) in courseResources" :key="r.id || i" class="resource-row">
              <el-tag size="mini" class="resource-type-tag">{{ resourceTypeLabel(r.type) }}</el-tag>
              <span class="resource-title">{{ r.title }}</span>
              <a v-if="r.url" :href="r.url" target="_blank" class="resource-url">
                <i class="el-icon-link"></i>
              </a>
              <el-button type="danger" size="mini" icon="el-icon-delete" circle
                @click="deleteCourseResource(r.id)" :loading="deletingResourceId === r.id" />
            </div>
            <div v-if="!courseResources.length" class="resource-empty">
              暂无参考资料
            </div>
          </div>

          <!-- 新增资源 -->
          <div class="resource-add-bar">
            <el-input v-model="resourceForm.title" placeholder="标题" size="small" class="res-input-title" />
            <el-select v-model="resourceForm.type" size="small" class="res-input-type">
              <el-option label="🔗 链接" value="LINK" />
              <el-option label="🎬 视频" value="VIDEO" />
              <el-option label="✏️ 练习" value="EXERCISE" />
              <el-option label="📖 参考" value="REFERENCE" />
              <el-option label="📥 下载" value="DOWNLOAD" />
            </el-select>
            <el-input v-model="resourceForm.url" placeholder="URL" size="small" class="res-input-url" />
            <el-button type="primary" size="small" @click="handleAddResource"
              :loading="addingResource" icon="el-icon-plus">添加</el-button>
          </div>
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="showCourseEditDialog=false">取消</el-button>
        <el-button type="primary" @click="handleUpdateCourse" :loading="courseUpdating">保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import MarkdownRenderer from '../../components/MarkdownRenderer.vue';
import { adminApi } from '../../api';
import { CONTENT_TYPE_LABEL, RESOURCE_TYPE_LABEL } from '../../constants';
export default {
  name: 'AdminPaths',
  components: { MarkdownRenderer },
  data() {
    return {
      paths: [],
      loading: true,
      currentPage: 1,
      pageSize: 10,
      total: 0,
      // 新建
      showCreateDialog: false,
      creating: false,
      form: { title: '', description: '', difficulty: 'BEGINNER', status: 'DRAFT' },
      // 编辑
      showEditDialog: false,
      editing: null,
      editForm: {},
      updating: false,
      // AI 生成
      showGenerateDialog: false,
      generating: false,
      generateRequirement: '',
      // ===== 课程管理 =====
      showCourseDialog: false,
      currentPathId: null,
      currentPathTitle: '',
      courses: [],
      coursesLoading: false,
      // 新增课程
      showCourseCreateDialog: false,
      courseSaving: false,
      courseForm: {
        title: '', description: '', contentType: 'ARTICLE',
        estimatedMinutes: 30, orderIndex: 1, contentMarkdown: '', _previewMode: 'edit'
      },
      // 编辑课程
      showCourseEditDialog: false,
      courseUpdating: false,
      courseEditForm: {},
      // 课程资源（编辑对话框内）
      courseResources: [],
      courseResourcesLoading: false,
      deletingResourceId: null,
      resourceForm: { title: '', type: 'LINK', url: '', description: '' },
      addingResource: false
    };
  },
  created() { this.load(); },
  methods: {
    async load() {
      this.loading = true;
      const res = await adminApi.getPaths({ page: this.currentPage, pageSize: this.pageSize });
      if (res.code === 200) {
        this.paths = res.data.list || [];
        this.total = res.data.total || 0;
      }
      this.loading = false;
    },
    async handlePageChange(page) {
      this.currentPage = page;
      await this.load();
    },
    // ---- 新建 ----
    async handleCreate() {
      this.creating = true;
      await adminApi.createPath(this.form);
      this.$message.success('创建成功');
      this.showCreateDialog = false;
      this.creating = false;
      this.form = { title: '', description: '', difficulty: 'BEGINNER', status: 'DRAFT' };
      this.load();
    },
    // ---- 编辑 ----
    handleEdit(row) {
      this.editForm = {
        id: row.id,
        title: row.title,
        description: row.description,
        difficulty: row.difficulty,
        status: row.status,
        estimatedDays: row.estimatedDays
      };
      this.showEditDialog = true;
    },
    async handleUpdate() {
      this.updating = true;
      await adminApi.updatePath(this.editForm);
      this.$message.success('保存成功');
      this.showEditDialog = false;
      this.updating = false;
      this.load();
    },
    // ---- 删除 ----
    async handleDelete(id) {
      await this.$confirm('确认删除该学习路径？此操作不可恢复。', '确认删除', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      });
      await adminApi.deletePath(id);
      this.$message.success('删除成功');
      this.load();
    },
    // ---- AI 生成 ----
    async handleGenerate() {
      if (!this.generateRequirement.trim()) {
        this.$message.warning('请输入需求描述');
        return;
      }
      this.generating = true;
      try {
        const res = await adminApi.generatePath({ requirement: this.generateRequirement });
        if (res.code === 200) {
          this.$message.success('AI 生成成功！已保存为草稿');
          this.showGenerateDialog = false;
          this.generateRequirement = '';
          this.load();
        } else {
          this.$message.error(res.message || '生成失败');
        }
      } catch (e) {
        this.$message.error(e?.response?.data?.message || 'AI 服务调用失败，请检查 API 配置');
      } finally {
        this.generating = false;
      }
    },
    // ===== 课程管理 =====
    async handleManageCourses(row) {
      this.currentPathId = row.id;
      this.currentPathTitle = row.title;
      this.showCourseDialog = true;
      await this.loadCourses();
    },
    async loadCourses() {
      if (!this.currentPathId) return;
      this.coursesLoading = true;
      const res = await adminApi.getPathCourses(this.currentPathId);
      if (res.code === 200) {
        this.courses = res.data || [];
      }
      this.coursesLoading = false;
    },
    // ---- 新增课程 ----
    async handleCreateCourse() {
      if (!this.courseForm.title.trim()) {
        this.$message.warning('请输入课程标题');
        return;
      }
      this.courseSaving = true;
      try {
        await adminApi.createCourse(this.currentPathId, this.courseForm);
        this.$message.success('课程创建成功');
        this.showCourseCreateDialog = false;
        this.courseForm = {
          title: '', description: '', contentType: 'ARTICLE',
          estimatedMinutes: 30, orderIndex: 1, contentMarkdown: '', _previewMode: 'edit'
        };
        await this.loadCourses();
      } catch {
        this.$message.error('创建失败');
      } finally {
        this.courseSaving = false;
      }
    },
    // ---- 编辑课程 ----
    editCourse(row) {
      this.courseEditForm = { ...row, _previewMode: 'edit' };
      this.showCourseEditDialog = true;
      this.loadCourseResources(row.id);
    },
    async handleUpdateCourse() {
      if (!this.courseEditForm.title.trim()) {
        this.$message.warning('请输入课程标题');
        return;
      }
      this.courseUpdating = true;
      try {
        await adminApi.updateCourse(this.courseEditForm);
        this.$message.success('课程更新成功');
        this.showCourseEditDialog = false;
        await this.loadCourses();
      } catch {
        this.$message.error('更新失败');
      } finally {
        this.courseUpdating = false;
      }
    },
    // ---- 删除课程 ----
    async deleteCourse(id) {
      await this.$confirm('确认删除该课程？', '确认删除', {
        confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
      });
      await adminApi.deleteCourse(id);
      this.$message.success('删除成功');
      await this.loadCourses();
    },
    // ===== 课程资源管理 =====
    resourceTypeLabel(type) {
      return RESOURCE_TYPE_LABEL[type] || type;
    },
    contentTypeLabel(type) {
      return CONTENT_TYPE_LABEL[type] || type;
    },
    async loadCourseResources(courseId) {
      if (!courseId) return;
      this.courseResourcesLoading = true;
      const res = await adminApi.getCourseResources(courseId);
      if (res.code === 200) {
        this.courseResources = res.data || [];
      }
      this.courseResourcesLoading = false;
    },
    async handleAddResource() {
      if (!this.resourceForm.title.trim() || !this.resourceForm.url.trim()) {
        this.$message.warning('请填写标题和 URL');
        return;
      }
      this.addingResource = true;
      try {
        await adminApi.addCourseResource(this.courseEditForm.id, this.resourceForm);
        this.$message.success('参考资料已添加');
        this.resourceForm = { title: '', type: 'LINK', url: '', description: '' };
        await this.loadCourseResources(this.courseEditForm.id);
      } catch {
        this.$message.error('添加失败');
      } finally {
        this.addingResource = false;
      }
    },
    async deleteCourseResource(id) {
      try {
        this.deletingResourceId = id;
        await adminApi.deleteCourseResource(id);
        this.$message.success('已删除');
        await this.loadCourseResources(this.courseEditForm.id);
      } catch {
        this.$message.error('删除失败');
      } finally {
        this.deletingResourceId = null;
      }
    }
  }
};
</script>

<style scoped>
.table-pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
.generate-hint {
  font-size: 13px;
  color: var(--text-tertiary, #94a3b8);
  line-height: 1.6;
  display: flex;
  align-items: flex-start;
  gap: 6px;
}
.generate-hint i {
  font-size: 15px;
  margin-top: 2px;
  flex-shrink: 0;
}

/* Markdown 编辑器切换栏 */
.markdown-editor-tabs {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}
.editor-hint {
  font-size: 12px;
  color: var(--text-tertiary, #94a3b8);
}
.markdown-preview {
  border: 1px solid var(--border, #e2e8f0);
  border-radius: 6px;
  padding: 16px 20px;
  min-height: 300px;
  background: #fafbfc;
  max-height: 480px;
  overflow-y: auto;
}

/* 参考资料管理 */
.resource-list {
  border: 1px solid var(--border, #e2e8f0);
  border-radius: 6px;
  padding: 10px 12px;
  min-height: 40px;
  background: #fafbfc;
  margin-bottom: 10px;
}
.resource-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 0;
  border-bottom: 1px solid #f1f5f9;
}
.resource-row:last-child { border-bottom: none; }
.resource-type-tag { flex-shrink: 0; }
.resource-title { flex: 1; font-size: 13px; }
.resource-url {
  color: var(--primary, #3b82f6);
  font-size: 16px;
  text-decoration: none;
}
.resource-empty {
  text-align: center;
  color: var(--text-tertiary, #94a3b8);
  font-size: 13px;
  padding: 10px 0;
}
.resource-add-bar {
  display: flex;
  gap: 8px;
  align-items: center;
}
.res-input-title { width: 140px; }
.res-input-type { width: 100px; }
.res-input-url { flex: 1; }
</style>
