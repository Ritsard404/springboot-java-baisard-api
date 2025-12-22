package com.ritsard.baisard.file.dto.v2;

public class FileStatisticsDto {
    private String category;
    private Long count;
    private Long totalSize;
    private Double averageSize;

    public FileStatisticsDto(String category, Long count, Long totalSize) {
        this.category = category;
        this.count = count;
        this.totalSize = totalSize;
    }

    public FileStatisticsDto(Double averageSize, Long count) {
        this.averageSize = averageSize;
        this.count = count;
    }

    public FileStatisticsDto(Object category, Long count, Long totalSize) {
        this.category = String.valueOf(category);
        this.count = count;
        this.totalSize = totalSize;
    }

    public String getCategory() {
        return this.category;
    }

    public Long getCount() {
        return this.count;
    }

    public Long getTotalSize() {
        return this.totalSize;
    }

    public Double getAverageSize() {
        return this.averageSize;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public void setTotalSize(Long totalSize) {
        this.totalSize = totalSize;
    }

    public void setAverageSize(Double averageSize) {
        this.averageSize = averageSize;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof FileStatisticsDto)) {
            return false;
        }
        FileStatisticsDto other = (FileStatisticsDto)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Long this$count = this.getCount();
        Long other$count = other.getCount();
        if (this$count == null ? other$count != null : !((Object)this$count).equals(other$count)) {
            return false;
        }
        Long this$totalSize = this.getTotalSize();
        Long other$totalSize = other.getTotalSize();
        if (this$totalSize == null ? other$totalSize != null : !((Object)this$totalSize).equals(other$totalSize)) {
            return false;
        }
        Double this$averageSize = this.getAverageSize();
        Double other$averageSize = other.getAverageSize();
        if (this$averageSize == null ? other$averageSize != null : !((Object)this$averageSize).equals(other$averageSize)) {
            return false;
        }
        String this$category = this.getCategory();
        String other$category = other.getCategory();
        return !(this$category == null ? other$category != null : !this$category.equals(other$category));
    }

    protected boolean canEqual(Object other) {
        return other instanceof FileStatisticsDto;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Long $count = this.getCount();
        result = result * 59 + ($count == null ? 43 : ((Object)$count).hashCode());
        Long $totalSize = this.getTotalSize();
        result = result * 59 + ($totalSize == null ? 43 : ((Object)$totalSize).hashCode());
        Double $averageSize = this.getAverageSize();
        result = result * 59 + ($averageSize == null ? 43 : ((Object)$averageSize).hashCode());
        String $category = this.getCategory();
        result = result * 59 + ($category == null ? 43 : $category.hashCode());
        return result;
    }

    public String toString() {
        return "FileStatisticsDto(category=" + this.getCategory() + ", count=" + this.getCount() + ", totalSize=" + this.getTotalSize() + ", averageSize=" + this.getAverageSize() + ")";
    }

    public FileStatisticsDto() {
    }

    public FileStatisticsDto(String category, Long count, Long totalSize, Double averageSize) {
        this.category = category;
        this.count = count;
        this.totalSize = totalSize;
        this.averageSize = averageSize;
    }
}

