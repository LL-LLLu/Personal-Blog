import axios from "@/axios";

// Get wiki list (frontend)
export function getWikiList() {
    return axios.post("/wiki/list")
}

// Get wiki detail
export function getWikiDetail(id) {
    return axios.post("/wiki/detail", {id})
}