package com.assignment.test.dto.informationdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BannerDto {

  private String banner_name;
  private String banner_image;
  private String description;

}
