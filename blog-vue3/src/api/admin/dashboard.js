import axios from "@/axios";

// Get dashboard basic statistics (article count, category count, tag count, total views)
export function getBaseStatisticsInfo() {
    return axios.post("/admin/dashboard/statistics")
}

// Get dashboard article publish heatmap statistics
export function getPublishArticleStatisticsInfo() {
    return axios.post("/admin/dashboard/publishArticle/statistics")
}

// 获取仪表盘最近一周 PV 访问量信息
export function getArticlePVStatisticsInfo() {
    return axios.post("/admin/dashboard/pv/statistics")
}