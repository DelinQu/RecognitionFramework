package com.lin.Repository;

import com.lin.entity.Picture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public  interface  PictureRepository extends JpaRepository<Picture, Integer> {
}
