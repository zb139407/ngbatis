package ye.weicheng.ngbatis.demo;

// Copyright (c) 2022 All project authors. All rights reserved.
//
// This source code is licensed under Apache 2.0 License.

import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.util.Assert;
import org.nebula.contrib.ngbatis.utils.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ye.weicheng.ngbatis.demo.pojo.Like;
import ye.weicheng.ngbatis.demo.pojo.LikeWithRank;
import ye.weicheng.ngbatis.demo.pojo.Person;
import ye.weicheng.ngbatis.demo.repository.TestRepository;

/**
 * @author yeweicheng
 * @since 2022-06-13 0:47
 * <br>Now is history!
 */
@SpringBootTest
public class NebulaBasicDaoTests {

  @Autowired
  private TestRepository repository;

  // region query zoom
  @Test
  public void selectById() {
    String id = "赵小洋";
    Person person = repository.selectById(id);
    System.out.println(JSON.toJSONString(person));
  }

  @Test
  public void selectByIds() {
    List<String> ids = Arrays.asList("刘小北","王小冰", "赵小洋");
    List<Person> people = repository.selectByIds(ids);
    System.out.println(JSON.toJSONString(people));
  }

  @Test
  public void selectBySelective() {
    Person person = new Person();
    person.setName("赵小洋");
    person.setAge(18);
    person.setBirthday(new Date());
    List<Person> people = repository.selectBySelective(person);
    System.out.println(JSON.toJSONString(people));
  }
  
  /**
   * https://github.com/nebula-contrib/ngbatis/issues/35.
   */
  @Test
  public void selectBySelectiveWhenIdIsNull() {
    Person person = new Person();
    person.setAge(18);
    List<Person> people = repository.selectBySelective(person);
    System.out.println(JSON.toJSONString(people));
  }

  @Test
  public void selectBySelectiveStringLike() {
    Person person = new Person();
    person.setName("赵小洋");
    person.setAge(18);
    person.setGender("F");
    person.setBirthday(new Date());
    List<Person> people = repository.selectBySelectiveStringLike(person);
    System.out.println(JSON.toJSONString(people));
  }

  @Test
  public void selectIdBySelective() {
    Person person = new Person();
    person.setAge(18);
    List<String> people = repository.selectIdBySelective(person);
    System.out.println(people);
  }

  @Test
  public void selectIdBySelectiveStringLike() {
    Person person = new Person();
    person.setAge(18);
    person.setGender("F");
    List<String> people = repository.selectIdBySelectiveStringLike(person);
    System.out.println(people);
  }
  
  @Test
  public void selectByMap() {
    Map<String, Object> query = new HashMap<>();
    query.put("gender", "F");
    List<Person> people = repository.selectByMap(query);
    System.out.println(JSON.toJSONString(people));
  }

  @Test
  public void countByMap() {
    Map<String, Object> query = new HashMap<>();
    query.put("gender", "F");
    Long peopleCount = repository.countByMap(query);
    System.out.println(peopleCount);
  }

  @Test
  public void selectPage() {
    Page<Person> page = new Page<>();
    page.setPageNo(1);
    page.setPageSize(30);
    Person entity = new Person();
    entity.setAge(18);
    page.setEntity(entity);
    repository.selectPage(page);
    System.out.println(JSON.toJSONString(page));
  }
  // endregion

  // region insert zoom
  @Test
  public void insert() {
    Person person = new Person();
    person.setAge(null);
    person.setName("赵小洋");
    repository.insert(person);
  }

  @Test
  public void insertSelective() {
    Person person = new Person();
    person.setAge(20);
    person.setName("王小冰");
    repository.insertSelective(person);
  }

  @Test
  public void insertSelectiveWithDate() {
    Person person = new Person();
    person.setBirthday(new Date());
    person.setName("丁小碧");
    repository.insertSelective(person);
  }

  @Test
  public void insertBatch() {
    long now = System.currentTimeMillis();
    
    Person person1 = new Person();
    person1.setName("IB" + now);
    person1.setGender("M");
    person1.setAge(18);

    Person person2 = new Person();
    person2.setName("IB" + (now + 1));
    person2.setAge(18);
    person2.setBirthday(new Date());
    
    Person person3 = new Person();
    person3.setName("IB" + (now + 2));
    person3.setGender("M");
    person3.setBirthday(new Date());

    List<Person> people = new ArrayList<>();
    people.add(person1);
    people.add(person2);;
    people.add(person3);
    
    repository.insertBatch(people);
  }
  // endregion

  // region update zoom
  @Test
  public void updateById() {
    long now = System.currentTimeMillis();
    String name = "UBI" + now;
    Person person = new Person();
    person.setName(name);
    repository.insert(person);
    
    Integer newAge = randomAge();
    person.setAge(newAge);
    person.setGender("F");
    repository.updateById(person);
    
    Person personDb = repository.selectById(name);
    
    Assert.isTrue(newAge.equals(personDb.getAge()));
  }

  @Test
  public void updateByIdSelective() {
    long now = System.currentTimeMillis();
    String gender = "F";
    String name = "UBIS" + now;
    Person person = new Person();
    person.setName(name);
    person.setGender(gender);
    repository.insert(person);

    Integer newAge = randomAge();
    person.setAge(newAge);
    repository.updateByIdSelective(person);

    Person personDb = repository.selectById(name);
    Assert.equals(gender, personDb.getGender());
    Assert.equals(newAge, personDb.getAge());
  }

  @Test
  public void updateByIdBatch() {
    long now = System.currentTimeMillis();

    Person person1 = new Person();
    person1.setName("UBB" + now);
    person1.setGender("M");
    person1.setAge(18);

    Person person2 = new Person();
    person2.setName("UBB" + (now + 1));
    person2.setAge(18);
    person2.setBirthday(new Date());

    Person person3 = new Person();
    person3.setName("UBB" + (now + 2));
    person3.setGender("M");
    person3.setBirthday(new Date());

    List<Person> people = new ArrayList<>();
    people.add(person1);
    people.add(person2);;
    people.add(person3);

    repository.insertBatch(people);

    Integer newAge = randomAge();
    person1.setAge(newAge);

    Integer newAge2 = randomAge();
    person2.setAge(newAge2);
    
    Integer newAge3 = randomAge();
    person3.setAge(newAge3);
    
    repository.updateByIdBatchSelective(people);
    
    List<String> ids = people.stream().map(Person::getName).collect(Collectors.toList());
    List<Person> peopleDb = repository.selectByIds(ids);

    Assert.isTrue(peopleDb.size() == 3);
    
    for (Person personDb : peopleDb) {
      for (Person person : people) {
        if (Objects.equals(personDb.getName(), person.getName())) {
          Assert.isTrue(Objects.equals(person.getAge(), personDb.getAge()));
        }
      }
    }
    System.out.println(peopleDb);
  }
  // endregion

  // region delete zoom
  @Test
  public void deleteWithEdgeById() {
    int row = repository.deleteWithEdgeById("赵小洋");
    System.out.println(row);
  }

  @Test
  public void deleteById() {
    int row = repository.deleteById("赵小洋");
    System.out.println(row);
  }
  // endregion

  // region graph special
  @Test
  public void insertEdge() {
    Person person1 = new Person();
    person1.setName("赵小洋");
    repository.insertSelective(person1);

    Person person2 = new Person();
    person2.setName("易小海");
    repository.insertSelective(person2);

    Like like = new Like();

    repository.insertEdge(person1, like, person2);
  }

  @Test
  public void insertEdgeWithRank() {
    Person person1 = new Person();
    person1.setName("叶小南");
    repository.insertSelective(person1);

    Person person2 = new Person();
    repository.insertSelective(person2);

    LikeWithRank like = new LikeWithRank();

    repository.insertEdge(person1, like, person2);
  }

  @Test
  public void insertEdgeWithProps() {
    Person person1 = new Person();
    person1.setName("吴小极");
    repository.insertSelective(person1);

    Person person2 = new Person();
    person2.setName("刘小洲");
    repository.insertSelective(person2);

    Like like = new Like();
    like.setLikeness(0.7);

    repository.insertEdge(person1, like, person2);
  }

  @Test
  public void insertEdgeUseNodeId() {
    Like like = new Like();
    like.setLikeness(0.202210171102);
    repository.insertEdge("吴小极", like, "刘小洲");
  }
  
  @Test
  public void insertEdgeUseNodeId2() {
    LikeWithRank like = new LikeWithRank();
    like.setLikeness(0.202210171111);
    repository.insertEdge("吴小极", like, "刘小洲");
  }

  @Test
  public void insertEdgeWithPropsAndRank() {
    Person person1 = new Person();
    person1.setName("李大印");
    repository.insertSelective(person1);

    Person person2 = new Person();
    person2.setName("王小雪");
    repository.insertSelective(person2);

    LikeWithRank likeWithRank = new LikeWithRank();
    likeWithRank.setLikeness(0.7);

    repository.insertEdge(person1, likeWithRank, person2);
  }


  @Test
  public void existsEdge() {
    Boolean existsEdge = repository.existsEdge("李大印", Like.class, "丁小碧");
    System.out.println(existsEdge);
  }

  @Test
  public void listStartNodes() {
    List<Person> personList = repository.listStartNodes(Like.class, "易小海");
    System.out.println(JSON.toJSONString(personList));
  }

  @Test
  public void startNode() {
    Person whoIsStartForTest = repository.startNode(Like.class, "易小海");
    System.out.println(JSON.toJSONString(whoIsStartForTest));
  }
  // endregion

  @Test
  public void pkGeneratorTest() {
    Person person = new Person();
    repository.insertSelective(person);
    System.out.println(JSON.toJSONString(person));
  }

  private int randomAge() {
    double randomAge = Math.random() * 140;
    return (int) randomAge;
  }
}
