import javax.swing.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ParallelCollisionDetector extends Thread {

  private final PriorityQueueInterface<Object2D> sortedPoints;
  private final AABB region;
  private final QuadTree quadTree;
  private AtomicBoolean collisionFree;
  private Lock lock = new ReentrantLock();

  public ParallelCollisionDetector(
          PriorityQueueInterface<Object2D> sortedPoints,
          AABB region,
          QuadTree quadTree,
          AtomicBoolean collisionFree) {
    this.sortedPoints = sortedPoints;
    this.region = region;
    this.quadTree = quadTree;
    this.collisionFree = collisionFree;
  }

  @Override
  public void run() {
    checkObjects();
  }

//  Is this a fine-grained implementation?
//  Bug here due to reassigning collisionFree does not change the
//  value in the critical resource
//  Decided to go with AtomicBoolean because I needed a class instead
//  of primitive type so that I can change its internal values without
//  reassigning it which overwrite it
//  At first I just put synchronised on the method signature
//  Then I used a fine-grained solution(?) by locking only the area
//  which accesses the critical resource
//  To be fair, there is no concurrency is the first method because
//  the first thread goes into the while loop until sortedPoints.isEmpty
//  and then the rest of the threads do no work
//  Task: figure out how to perform optimistic locking on this
//  * Model answer did similar things, but it did synchronized (sortedPoints),
//  * it uses the lock on sortedPoints. It also synchronised the last if else statement
//  * as there might be race condition in quadTree
//  * e.g. if thread a checks that there is no collision, and then thread b added an object
//  * to the checked safeRegion, thread a will then add the object to that area without
//  * detecting any collisions.
  private void checkObjects() {
//    Bug: I wrote || instead of && in the while loop condition
    while (!sortedPoints.isEmpty() && collisionFree.get()) {
      Object2D currObject;
      lock.lock();
      try {
        currObject = sortedPoints.peek();
        sortedPoints.remove();
      } finally {
        lock.unlock();
      }

      Point2D topLeft = new Point2D(currObject.getCenter().x - currObject.getSize(),
              currObject.getCenter().y + currObject.getSize());
      Point2D bottomRight = new Point2D(currObject.getCenter().x + currObject.getSize(),
              currObject.getCenter().y - currObject.getSize());
      AABB safetyRegion = new AABB(topLeft, bottomRight);

//      * Incorrect
      if (quadTree.queryRegion(safetyRegion).isEmpty()) {
        quadTree.add(currObject);
      } else {
        collisionFree.set(false);
      }
    }
  }

//  The first alternative is coarse-grained solution, such as by
//  putting synchronized in the method declaration. However, there
//  would be no parallelism at all because the first thread would just
//  go into the while loop until it processed all the sorted points in
//  the priority queue, leaving no work for the rest of threads (i.e.
//  they will not enter the while loop at all).
//  The second alternative is optimistic. It does not use locks but instead
//  checks whether currObject is still in the critical resource, sortedPoints,
//  and deletes it if it is still in.
//  The disadvantage is that it might take very long if there are
//  many collisions.
}

// Total time: 6:55