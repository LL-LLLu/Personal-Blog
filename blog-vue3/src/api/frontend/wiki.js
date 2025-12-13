import axios from "@/axios";

// Get wiki list
export function getWikiList() {
    return axios.post("/wiki/list")
}

// Get wiki detail
export function getWikiDetail(id) {
    return axios.post("/wiki/detail", {id})
}

// Get wiki article pre/next
export function getWikiArticlePreNext(data) {
    return axios.post("/wiki/article/preNext", data)
}