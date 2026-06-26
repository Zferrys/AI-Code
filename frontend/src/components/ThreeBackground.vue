<template>
  <canvas ref="canvas" class="three-bg-canvas" :style="{ opacity }"></canvas>
</template>

<script>
import * as THREE from 'three';

export default {
  name: 'ThreeBackground',
  props: {
    opacity: { type: Number, default: 0.6 },
    /** 'hero' — 密集丰富（首页使用）；'subtle' — 轻量简洁（全站背景） */
    mode: { type: String, default: 'hero', validator: v => ['hero', 'subtle'].includes(v) },
    /** 固定的颜色主题，不传则随机使用品牌色 */
    colorTheme: { type: String, default: '' }
  },
  data() {
    return { frameId: null };
  },
  mounted() {
    this.initScene();
  },
  beforeDestroy() {
    this.cleanup();
  },
  methods: {
    initScene() {
      const canvas = this.$refs.canvas;
      const parent = canvas.parentElement;
      const width = parent.clientWidth;
      const height = parent.clientHeight;

      // 渲染器
      const renderer = new THREE.WebGLRenderer({ canvas, antialias: true, alpha: true });
      renderer.setSize(width, height);
      renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
      renderer.toneMapping = THREE.ACESFilmicToneMapping;
      renderer.toneMappingExposure = 1.2;

      // 场景和相机
      const scene = new THREE.Scene();
      const camera = new THREE.PerspectiveCamera(50, width / height, 0.1, 100);
      camera.position.set(0, 1, 8);

      const isSubtle = this.mode === 'subtle';

      // === 品牌色板 ===
      const palette = [
        { color: 0x3b82f6, emissive: 0x3b82f6, ei: 0.4 },  // 蓝
        { color: 0x0ea5e9, emissive: 0x0ea5e9, ei: 0.35 },  // 天蓝
        { color: 0x6366f1, emissive: 0x6366f1, ei: 0.3 },   // 靛蓝
        { color: 0x06b6d4, emissive: 0x06b6d4, ei: 0.25 },   // 青
        { color: 0x60a5fa, emissive: 0x60a5fa, ei: 0.3 },   // 天蓝
        { color: 0x38bdf8, emissive: 0x38bdf8, ei: 0.2 },   // 淡天蓝
      ];

      const materials = palette.map(p => new THREE.MeshStandardMaterial({
        color: p.color,
        emissive: p.emissive,
        emissiveIntensity: p.ei,
        roughness: 0.25,
        metalness: 0.35,
        transparent: true,
        opacity: isSubtle ? 0.6 : 1
      }));

      // 额外金属材质
      const matMetal = new THREE.MeshStandardMaterial({
        color: 0x94a3b8, roughness: 0.1, metalness: 0.9,
        transparent: true, opacity: isSubtle ? 0.4 : 0.8
      });
      const matGold = new THREE.MeshStandardMaterial({
        color: 0xf59e0b, roughness: 0.15, metalness: 0.85,
        emissive: 0xf59e0b, emissiveIntensity: 0.15,
        transparent: true, opacity: isSubtle ? 0.4 : 0.9
      });

      const allMats = [...materials, matMetal, matGold];

      // === 浮动几何体 ===
      const count = isSubtle ? 10 : 22;
      const shapes = [];
      const geoTypes = [
        () => new THREE.IcosahedronGeometry(0.2 + Math.random() * 0.3, 1),
        () => new THREE.TorusKnotGeometry(0.15 + Math.random() * 0.22, 0.04, 30, 6),
        () => new THREE.OctahedronGeometry(0.18 + Math.random() * 0.28, 0),
        () => new THREE.TetrahedronGeometry(0.2 + Math.random() * 0.3, 0),
        () => new THREE.DodecahedronGeometry(0.18 + Math.random() * 0.25, 0),
        () => new THREE.TorusGeometry(0.2 + Math.random() * 0.28, 0.035, 12, 24),
      ];

      for (let i = 0; i < count; i++) {
        const geo = geoTypes[i % geoTypes.length]();
        const mat = allMats[i % allMats.length];
        const mesh = new THREE.Mesh(geo, mat);
        const spread = isSubtle ? 8 : 12;
        mesh.position.set(
          (Math.random() - 0.5) * spread,
          (Math.random() - 0.5) * (isSubtle ? 5 : 8),
          (Math.random() - 0.5) * (isSubtle ? 3 : 5)
        );
        const scaleFactor = isSubtle ? 0.6 + Math.random() * 0.5 : 0.8 + Math.random() * 0.6;
        mesh.scale.set(scaleFactor, scaleFactor, scaleFactor);
        mesh.userData = {
          rotSpeed: new THREE.Vector3(
            (Math.random() - 0.5) * (isSubtle ? 0.006 : 0.012),
            (Math.random() - 0.5) * (isSubtle ? 0.006 : 0.012),
            (Math.random() - 0.5) * (isSubtle ? 0.006 : 0.012)
          ),
          floatSpeed: 0.3 + Math.random() * 0.5,
          floatAmp: isSubtle ? 0.3 + Math.random() * 0.5 : 0.4 + Math.random() * 0.8,
          startY: mesh.position.y,
          startX: mesh.position.x,
          wobbleX: Math.random() * Math.PI * 2
        };
        shapes.push(mesh);
        scene.add(mesh);
      }

      // === 旋转光环 ===
      if (!isSubtle) {
        const rings = [
          { radius: 3.2, tube: 0.035, color: 0x3b82f6, emissive: 0x3b82f6 },
          { radius: 2.4, tube: 0.025, color: 0x0ea5e9, emissive: 0x0ea5e9 },
          { radius: 1.7, tube: 0.02, color: 0x6366f1, emissive: 0x6366f1 },
        ];
        rings.forEach((r, i) => {
          const ring = new THREE.Mesh(
            new THREE.TorusGeometry(r.radius, r.tube, 12, 80),
            new THREE.MeshStandardMaterial({
              color: r.color, emissive: r.emissive, emissiveIntensity: 0.3,
              transparent: true, opacity: 0.5
            })
          );
          ring.rotation.x = Math.PI / (i === 1 ? 3 : (i === 2 ? 4 : 2));
          ring.rotation.z = i * 0.3;
          scene.add(ring);
          ring.userData = { rotX: 0.002 + i * 0.001, rotY: 0.004 - i * 0.001, rotZ: 0.001 * i };
          shapes.push({ isRing: true, mesh: ring });
        });
      }

      // === 粒子场 ===
      const particleCount = isSubtle ? 80 : 180;
      const posArray = new Float32Array(particleCount * 3);
      const sizeArray = new Float32Array(particleCount);
      for (let i = 0; i < particleCount; i++) {
        const idx = i * 3;
        posArray[idx] = (Math.random() - 0.5) * (isSubtle ? 10 : 16);
        posArray[idx + 1] = (Math.random() - 0.5) * (isSubtle ? 6 : 10);
        posArray[idx + 2] = (Math.random() - 0.5) * (isSubtle ? 4 : 7);
        sizeArray[i] = 0.02 + Math.random() * (isSubtle ? 0.02 : 0.03);
      }
      const particlesGeo = new THREE.BufferGeometry();
      particlesGeo.setAttribute('position', new THREE.BufferAttribute(posArray, 3));
      particlesGeo.setAttribute('size', new THREE.BufferAttribute(sizeArray, 1));
      const particlesMat = new THREE.PointsMaterial({
        color: 0x3b82f6, size: isSubtle ? 0.025 : 0.035,
        transparent: true, opacity: isSubtle ? 0.2 : 0.4,
        blending: THREE.AdditiveBlending,
        sizeAttenuation: true
      });
      const particles = new THREE.Points(particlesGeo, particlesMat);
      scene.add(particles);

      // === 光照 ===
      scene.add(new THREE.AmbientLight(0x404060, 0.6));
      const sun = new THREE.DirectionalLight(0xfff5e0, 1.5);
      sun.position.set(6, 10, 6);
      scene.add(sun);
      scene.add(new THREE.DirectionalLight(0x4466ff, 0.4));

      // === 动画循环 ===
      const clock = new THREE.Clock();
      const animate = () => {
        this.frameId = requestAnimationFrame(animate);
        const t = clock.getElapsedTime();
        const speed = isSubtle ? 0.5 : 1;

        // 更新光环
        shapes.forEach((obj, i) => {
          if (obj.isRing) {
            obj.mesh.rotation.x += obj.mesh.userData.rotX * speed;
            obj.mesh.rotation.y += obj.mesh.userData.rotY * speed;
            obj.mesh.rotation.z += obj.mesh.userData.rotZ * speed;
          } else {
            obj.rotation.x += obj.userData.rotSpeed.x * speed;
            obj.rotation.y += obj.userData.rotSpeed.y * speed;
            obj.rotation.z += obj.userData.rotSpeed.z * speed;
            obj.position.y = obj.userData.startY
              + Math.sin(t * obj.userData.floatSpeed + i) * obj.userData.floatAmp;
            // 微弱的水平漂移
            obj.position.x = obj.userData.startX
              + Math.sin(t * obj.userData.floatSpeed * 0.5 + obj.userData.wobbleX) * 0.4;
          }
        });

        particles.rotation.y += 0.0005 * speed;
        particles.rotation.x += 0.0002 * speed;

        renderer.render(scene, camera);
      };
      animate();

      // 响应式
      this._resizeHandler = () => {
        const w = parent.clientWidth;
        const h = parent.clientHeight;
        renderer.setSize(w, h);
        camera.aspect = w / h;
        camera.updateProjectionMatrix();
      };
      window.addEventListener('resize', this._resizeHandler);

      this._renderer = renderer;
      this._scene = scene;
      this._shapes = shapes;
      this._particles = particles;
    },

    cleanup() {
      if (this.frameId) cancelAnimationFrame(this.frameId);
      if (this._resizeHandler) window.removeEventListener('resize', this._resizeHandler);
      const dispose = (obj) => {
        if (obj.geometry) obj.geometry.dispose();
        if (obj.material) obj.material.dispose();
      };
      if (this._shapes) {
        this._shapes.forEach(s => { if (!s.isRing) dispose(s); else dispose(s.mesh); });
      }
      if (this._particles) {
        this._particles.geometry.dispose();
        this._particles.material.dispose();
      }
      if (this._renderer) {
        this._renderer.dispose();
        this._renderer.forceContextLoss();
      }
    }
  }
};
</script>

<style scoped>
.three-bg-canvas {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
}
</style>
